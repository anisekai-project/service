package me.anisekai.toshiko.services;

import me.anisekai.toshiko.components.JdaStore;
import me.anisekai.toshiko.data.anime.AnimeImportResult;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.anime.*;
import me.anisekai.toshiko.exceptions.animes.AnimeNotFoundException;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.services.misc.TaskService;
import me.anisekai.toshiko.tasks.SendAnnouncementTask;
import me.anisekai.toshiko.tasks.UpdateAnnouncementTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AnimeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnimeService.class);

    private final AnimeRepository           repository;
    private final ApplicationEventPublisher publisher;

    public AnimeService(AnimeRepository repository, ApplicationEventPublisher publisher, TaskService taskService, JdaStore store) {

        this.repository = repository;
        this.publisher  = publisher;
    }

    public AnimeRepository getRepository() {

        return this.repository;
    }

    public Set<Anime> findByStatus(AnimeStatus status) {
        return this.repository.findAllByStatus(status);
    }

    /**
     * Retrieve the {@link Anime} associated to the provided id.
     *
     * @param id
     *         The {@link Anime} id.
     *
     * @return The associated {@link Anime}.
     *
     * @throws AnimeNotFoundException
     *         Thrown if no {@link Anime} entity matched the provided id.
     */
    public Anime getAnime(long id) {

        return this.repository.findById(id).orElseThrow(AnimeNotFoundException::new);
    }

    /**
     * Update the amount of episode watched the provided {@link Anime}. This will trigger the
     * {@link AnimeWatchedUpdatedEvent} event.
     * <p>
     * After the update event, if the {@link Anime#getWatched()} value is equals to {@link Anime#getTotal()} (but not
     * 0), the status of the {@link Anime} will be changed to {@link AnimeStatus#WATCHED} using
     * {@link #setStatus(Anime, AnimeStatus)}.
     *
     * @param anime
     *         The {@link Anime} to update.
     * @param watched
     *         The new amount of episode watched.
     *
     * @return The updated {@link Anime}.
     */
    public Anime setProgression(Anime anime, long watched) {

        LOGGER.info("setProgression: Anime {} new watched amount is {}", anime.getId(), watched);

        long oldValue = anime.getWatched();
        anime.setWatched(watched);

        // Value security
        if (anime.getWatched() > anime.getTotal() && anime.getTotal() > 0) {
            LOGGER.warn("Tried to apply watch value being greater than the amount of episode available.");
            LOGGER.debug("Forcing watch value to {}", anime.getTotal());
            anime.setWatched(anime.getTotal());
        }

        Anime saved = this.repository.save(anime);

        LOGGER.debug("Sending AnimeWatchedUpdatedEvent...");
        AnimeWatchedUpdatedEvent event = new AnimeWatchedUpdatedEvent(this, saved, oldValue, watched);
        this.publisher.publishEvent(event);

        if (saved.getWatched() == saved.getTotal() && saved.getTotal() != 0) {
            LOGGER.debug("The watch value is the same as the total available. Switching status to WATCHED.");
            // Reached the end
            return this.setStatus(saved, AnimeStatus.WATCHED);
        }

        return saved;
    }

    /**
     * Update the {@link AnimeStatus} for the provided {@link Anime}. This will trigger the
     * {@link AnimeStatusUpdatedEvent} event.
     *
     * @param anime
     *         The {@link Anime} to update.
     * @param status
     *         The new {@link AnimeStatus}
     *
     * @return The updated {@link Anime}
     */
    public Anime setStatus(Anime anime, AnimeStatus status) {

        LOGGER.info("setStatus: Anime {} new status is {}", anime.getId(), status.name());

        AnimeStatus oldValue = anime.getStatus();
        anime.setStatus(status);

        Anime saved = this.repository.save(anime);

        LOGGER.debug("Sending AnimeStatusUpdatedEvent....");
        AnimeStatusUpdatedEvent event = new AnimeStatusUpdatedEvent(this, saved, oldValue, status);
        this.publisher.publishEvent(event);
        return saved;
    }

    /**
     * Update the total amount of watchable episodes for the provided {@link Anime}. This will trigger the
     * {@link AnimeTotalUpdatedEvent} event.
     * <p>
     * This will not perform any check on {@link Anime#getWatched()} value. This means depending on the value provided,
     * {@link Anime#getWatched()} could be greater than {@link Anime#getTotal()}.
     *
     * @param anime
     *         The {@link Anime} to update.
     * @param total
     *         The new amount of watchable episodes.
     *
     * @return The updated {@link Anime}.
     */
    public Anime setTotal(Anime anime, long total) {

        LOGGER.info("setTotal: Anime {} new total is {}", anime.getId(), total);

        long oldValue = anime.getTotal();
        anime.setTotal(total);

        Anime saved = this.repository.save(anime);

        LOGGER.debug("Sending AnimeTotalUpdatedEvent....");
        AnimeTotalUpdatedEvent event = new AnimeTotalUpdatedEvent(this, saved, oldValue, total);
        this.publisher.publishEvent(event);
        return saved;
    }

    /**
     * Create or update an {@link Anime} entity using the provided {@link JSONObject}.
     *
     * @param user
     *         The {@link DiscordUser} that will be the owner for the {@link Anime} entity (if created)
     * @param json
     *         The {@link JSONObject} containing the {@link Anime} data.
     *
     * @return An {@link AnimeImportResult} instance.
     */
    public AnimeImportResult importAnime(DiscordUser user, JSONObject json) {

        LOGGER.info("importAnime: User {} is importing anime data...", user.getId());

        JSONArray    genreArray = json.getJSONArray("genres");
        JSONArray    themeArray = json.getJSONArray("themes");
        String       rawStatus  = json.getString("status");
        List<String> genres     = new ArrayList<>();
        List<String> themes     = new ArrayList<>();

        genreArray.forEach(obj -> genres.add(obj.toString()));
        themeArray.forEach(obj -> themes.add(obj.toString()));

        AnimeStatus status = AnimeStatus.from(rawStatus);
        long        total  = Long.parseLong(json.getString("episode"));
        long        time   = Long.parseLong(json.getString("time"));

        Anime loaded = new Anime();
        loaded.setName(json.getString("title"));
        loaded.setSynopsis(json.getString("synopsis"));
        loaded.setGenres(String.join(", ", genres));
        loaded.setThemes(String.join(", ", themes));
        loaded.setStatus(status);
        loaded.setLink(json.getString("link"));
        loaded.setImage(json.getString("image"));
        loaded.setWatched(0);
        loaded.setTotal(total);
        loaded.setEpisodeDuration(time == 0 ? 24 : time);
        loaded.setAddedBy(user);
        loaded.setAddedAt(ZonedDateTime.now().withNano(0));

        Optional<Anime> byName = this.repository.findByName(loaded.getName());

        if (byName.isPresent()) {
            LOGGER.info("Anime already exists with id {}. Data will be used to update.", byName.get().getId());
            Anime anime = byName.get();
            anime.patch(loaded);
            Anime saved = this.repository.save(anime);

            LOGGER.debug("Sending AnimeUpdatedEvent...");
            AnimeUpdatedEvent event = new AnimeUpdatedEvent(this, saved);
            this.publisher.publishEvent(event);
            return new AnimeImportResult(saved, AnimeImportResult.State.UPDATED);
        }

        LOGGER.info("Anime does not exist. Creating anime entry...");
        Anime saved = this.repository.save(loaded);

        LOGGER.debug("Sending AnimeCreatedEvent...");
        AnimeCreatedEvent event = new AnimeCreatedEvent(this, saved);
        this.publisher.publishEvent(event);

        return new AnimeImportResult(saved, AnimeImportResult.State.CREATED);
    }

    public void announce() {
        // Check anime announce status

        for (Anime anime : this.repository.findAll()) {
            if (anime.getAnnounceMessage() == null) {
                this.publisher.publishEvent(new AnimeCreatedEvent(this, anime));
            } else {
                this.publisher.publishEvent(new AnimeUpdatedEvent(this, anime));
            }
        }

    }

    /**
     * Retrieve the count of displayable {@link Anime} in the database.
     *
     * @return The count of displayable {@link Anime}.
     */
    public long getDisplayableAnimeCount() {

        return this.repository.findAllByStatusIn(AnimeStatus.getDisplayable()).size();
    }
}
