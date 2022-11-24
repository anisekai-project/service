package me.anisekai.toshiko.services;

import fr.alexpado.lib.rest.interfaces.IRestAction;
import io.sentry.Sentry;
import me.anisekai.toshiko.entities.*;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.AnimeUpdateType;
import me.anisekai.toshiko.enums.CronState;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.AnimeNightUpdateEvent;
import me.anisekai.toshiko.events.AnimeUpdateEvent;
import me.anisekai.toshiko.exceptions.JdaUnavailableException;
import me.anisekai.toshiko.exceptions.animes.AnimeAlreadyRegisteredException;
import me.anisekai.toshiko.exceptions.animes.AnimeNotFoundException;
import me.anisekai.toshiko.exceptions.interests.InterestLevelUnchangedException;
import me.anisekai.toshiko.exceptions.users.EmojiAlreadyUsedException;
import me.anisekai.toshiko.exceptions.users.InvalidEmojiException;
import me.anisekai.toshiko.helpers.FileDownloader;
import me.anisekai.toshiko.helpers.JdaStoreService;
import me.anisekai.toshiko.helpers.containers.InterestPower;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.repositories.*;
import me.anisekai.toshiko.utils.AnimeNights;
import me.anisekai.toshiko.utils.Animes;
import me.anisekai.toshiko.utils.DiscordUtils;
import me.anisekai.toshiko.utils.MapUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;

@Service
public class ToshikoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToshikoService.class);

    private final ApplicationEventPublisher publisher;
    private final JdaStoreService           store;
    private final AnimeRepository           animeRepository;
    private final UserRepository            userRepository;
    private final WatchlistRepository       watchlistRepository;
    private final InterestRepository        interestRepository;
    private final AnimeNightRepository      animeNightRepository;

    @Value("${toshiko.anime.server}")
    private long toshikoAnimeServer;


    public ToshikoService(ApplicationEventPublisher publisher, JdaStoreService store, AnimeRepository animeRepository, UserRepository userRepository, WatchlistRepository watchlistRepository, InterestRepository interestRepository, AnimeNightRepository animeNightRepository) {

        this.publisher            = publisher;
        this.store                = store;
        this.animeRepository      = animeRepository;
        this.userRepository       = userRepository;
        this.watchlistRepository  = watchlistRepository;
        this.interestRepository   = interestRepository;
        this.animeNightRepository = animeNightRepository;
    }

    // <editor-fold desc="Repositories">
    public AnimeRepository getAnimeRepository() {

        return this.animeRepository;
    }

    public UserRepository getUserRepository() {

        return this.userRepository;
    }

    public WatchlistRepository getWatchlistRepository() {

        return this.watchlistRepository;
    }

    public InterestRepository getInterestRepository() {

        return this.interestRepository;
    }

    public AnimeNightRepository getAnimeNightRepository() {

        return this.animeNightRepository;
    }

    // </editor-fold>

    // <editor-fold desc="Anime">

    public Anime setAnimeAnnouncementMessage(long id, Message message) {

        return this.setAnimeAnnouncementMessage(this.findAnime(id), message);
    }

    public Anime setAnimeAnnouncementMessage(Anime anime, Message message) {

        anime.setAnnounceMessage(message.getIdLong());
        return this.animeRepository.save(anime);
    }

    /**
     * Set the {@link Anime} progression.
     *
     * @param id
     *         The {@link Anime}'s id.
     * @param watched
     *         The amount of episode watched.
     *
     * @return The {@link Anime}, if the update has been successful.
     */
    public Anime setAnimeProgression(long id, long watched) {

        return this.setAnimeProgression(this.findAnime(id), watched);
    }

    /**
     * Set the {@link Anime} progression.
     *
     * @param anime
     *         The {@link Anime} to update.
     * @param watched
     *         The amount of episode watched.
     *
     * @return The updated {@link Anime}
     */
    public Anime setAnimeProgression(Anime anime, long watched) {

        return this.setAnimeProgression(anime, watched, anime.getTotal());
    }

    /**
     * Set the {@link Anime} progression.
     *
     * @param id
     *         The {@link Anime}'s id.
     * @param watched
     *         The amount of episode watched.
     * @param amount
     *         The total amount of episode available.
     *
     * @return The {@link Anime}, if the update has been successful.
     */
    public Anime setAnimeProgression(long id, long watched, long amount) {

        return this.setAnimeProgression(this.findAnime(id), watched, amount);
    }

    /**
     * Set the {@link Anime} progression.
     *
     * @param anime
     *         The {@link Anime} to update.
     * @param watched
     *         The amount of episode watched.
     * @param amount
     *         The total amount of episode available.
     *
     * @return The updated {@link Anime}
     */
    public Anime setAnimeProgression(Anime anime, long watched, long amount) {

        anime.setWatched(watched);
        anime.setTotal(amount);

        if (anime.getWatched() == anime.getTotal() && anime.getTotal() > 0) {
            this.setAnimeStatus(anime, AnimeStatus.WATCHED);
        } else {
            switch (anime.getStatus()) {
                case WATCHED, DOWNLOADED -> this.setAnimeStatus(anime, AnimeStatus.WATCHING);
                case SIMULCAST_AVAILABLE -> this.setAnimeStatus(anime, AnimeStatus.SIMULCAST);
                default -> this.queueUpdate(anime.getStatus());
            }
        }

        this.refreshAnimeAnnounce(anime);
        return this.animeRepository.save(anime);
    }

    /**
     * Set the {@link Anime} status.
     *
     * @param id
     *         The {@link Anime}'s id.
     * @param status
     *         The {@link AnimeStatus} to set.
     *
     * @return The {@link Anime}, if the update has been successful.
     */
    public Anime setAnimeStatus(long id, AnimeStatus status) {

        return this.setAnimeStatus(this.findAnime(id), status);
    }

    /**
     * Set the {@link Anime} status.
     *
     * @param anime
     *         The {@link Anime} to update.
     * @param status
     *         The {@link AnimeStatus} to set.
     *
     * @return The updated {@link Anime}
     */
    public Anime setAnimeStatus(Anime anime, AnimeStatus status) {

        if (anime.getStatus() == status) {
            return anime;
        }

        if (status == AnimeStatus.WATCHED && anime.getTotal() > 0) {
            anime.setWatched(anime.getTotal());
        }

        this.queueUpdate(anime.getStatus());
        anime.setStatus(status);
        this.queueUpdate(anime.getStatus());
        this.refreshAnimeAnnounce(anime);
        return this.animeRepository.save(anime);
    }

    /**
     * Retrieve the count of displayable {@link Anime} in the database.
     *
     * @return The count of displayable {@link Anime}.
     */
    public long getDisplayableAnimeCount() {

        return this.animeRepository.findAllByStatusIn(AnimeStatus.getDisplayable()).size();
    }

    /**
     * Send an event for the provided {@link Anime}
     *
     * @param anime
     *         The {@link Anime} source of the notification.
     */
    public void createAnimeAnnounce(Anime anime) {

        Anime reloaded = this.findAnime(anime.getId());
        this.publisher.publishEvent(new AnimeUpdateEvent(this, reloaded, reloaded.getAnnounceMessage() == null ? AnimeUpdateType.ADDED : AnimeUpdateType.UPDATE));
    }

    /**
     * Send an event for the provided {@link Anime}
     *
     * @param anime
     *         The {@link Anime} source of the notification.
     */
    public void refreshAnimeAnnounce(Anime anime) {

        Anime reloaded = this.findAnime(anime.getId());
        this.publisher.publishEvent(new AnimeUpdateEvent(this, reloaded, AnimeUpdateType.UPDATE));
    }

    /**
     * Create a new {@link Anime} entry from the provided {@link AnimeProvider}.
     *
     * @param user
     *         The owner of the soon-to-be newly created {@link Anime}.
     * @param provider
     *         The {@link AnimeProvider} containing the raw {@link Anime} data.
     * @param status
     *         The {@link AnimeStatus} of the soon-to-be newly created {@link Anime}.
     *
     * @return The newly created {@link Anime}
     */
    public Anime createAnime(User user, AnimeProvider provider, AnimeStatus status) {

        Optional<Anime> optionalAnime = this.animeRepository.findByName(provider.getName());

        if (optionalAnime.isPresent()) {
            throw new AnimeAlreadyRegisteredException(provider.getName());
        }

        DiscordUser discordUser = this.findUser(user);
        Anime       anime       = this.animeRepository.save(new Anime(discordUser, provider, status));
        this.setInterestLevel(anime, discordUser, InterestLevel.INTERESTED);
        this.createAnimeAnnounce(anime);
        return anime;
    }

    /**
     * Find an {@link Anime} by its id.
     *
     * @param id
     *         The {@link Anime}'s id.
     *
     * @return An {@link Anime}.
     */
    public Anime findAnime(long id) {

        return this.animeRepository.findById(id).orElseThrow(AnimeNotFoundException::new);
    }

    /**
     * Retrieve all vote score on {@link Anime} that support votes.
     *
     * @return A {@link Map} associating each {@link Anime} to their vote score.
     */
    public Map<Anime, Double> getAnimeVotes() {

        Map<Anime, Double>         votes         = new HashMap<>();
        InterestPower              interestPower = this.getInterestPower();
        Map<DiscordUser, Double>   power         = interestPower.getUserInterestPower();
        Map<Anime, List<Interest>> animeInterest = MapUtils.groupBy(interestPower.getInterests(), Interest::getAnime);

        animeInterest.forEach((anime, interests) -> {
            double points = interests.stream()
                                     .mapToDouble(interest -> interest.getValue(power))
                                     .sum();
            votes.put(anime, points);
        });

        return votes;
    }
    // </editor-fold>

    // <editor-fold desc="Interest">
    public Interest findInterest(Anime anime, DiscordUser user) {

        return this.interestRepository.findByAnimeAndUser(anime, user).orElseGet(() -> new Interest(anime, user));
    }

    public Interest setInterestLevel(long animeId, User user, InterestLevel level) {

        Anime       anime       = this.findAnime(animeId);
        DiscordUser discordUser = this.findUser(user);

        return this.setInterestLevel(anime, discordUser, level);
    }

    public Interest setInterestLevel(Anime anime, DiscordUser user, InterestLevel level) {

        Interest interest = this.findInterest(anime, user);

        if (interest.getLevel() == level) {
            throw new InterestLevelUnchangedException(interest);
        }

        interest.setLevel(level);
        this.queueUpdate(anime.getStatus());
        Interest saved = this.interestRepository.save(interest);
        this.refreshAnimeAnnounce(anime);
        return saved;
    }

    public InterestPower getInterestPower() {

        Map<DiscordUser, Double> userInterestPower = new HashMap<>();

        List<Interest> interests = this.interestRepository.findAllActive()
                                                          .stream()
                                                          .filter(interest -> interest.getLevel()
                                                                                      .getPowerModifier() != 0)
                                                          .filter(interest -> interest.getAnime()
                                                                                      .getStatus()
                                                                                      .isWatchable())
                                                          .toList();

        List<DiscordUser> users = interests.stream().map(Interest::getUser).distinct().toList();

        long totalVotes = interests.size();

        for (DiscordUser user : users) {
            long totalUserVote = interests.stream().filter(vote -> vote.getUser().equals(user)).count();
            userInterestPower.put(user, (double) totalUserVote / (double) totalVotes);
        }

        return new InterestPower(userInterestPower, interests);
    }
    // </editor-fold>

    // <editor-fold desc="User">
    public DiscordUser findUser(User user) {

        Optional<DiscordUser> optionalDiscordUser = this.userRepository.findById(user.getIdLong());
        DiscordUser           discordUser         = optionalDiscordUser.orElseGet(() -> new DiscordUser(user));
        return this.userRepository.save(discordUser);
    }

    public boolean setUserEmoji(User user, String emoji) {

        if (emoji.matches("\\w*")) {
            throw new InvalidEmojiException();
        }

        DiscordUser discordUser = this.findUser(user);

        if (emoji.equalsIgnoreCase(discordUser.getEmote())) {
            return false;
        }

        if (this.userRepository.findAll().stream().anyMatch(otherUser -> emoji.equals(otherUser.getEmote()))) {
            throw new EmojiAlreadyUsedException();
        }

        discordUser.setEmote(emoji);
        this.userRepository.save(discordUser);
        return true;
    }
    // </editor-fold>

    // <editor-fold desc="Watchlist">
    public void queueUpdate(AnimeStatus status) {

        this.queueUpdate(status, false);
    }

    public void queueUpdate(AnimeStatus status, Boolean force) {

        if (!AnimeStatus.getDisplayable().contains(status)) {
            return;
        }

        Optional<Watchlist> optionalWatchlist = this.watchlistRepository.findById(status);

        if (optionalWatchlist.isEmpty() && (force == null || !force)) {
            return;
        }

        Watchlist watchlist = optionalWatchlist.orElse(new Watchlist(status));
        watchlist.setState(CronState.REQUIRED);
        this.watchlistRepository.save(watchlist);
    }

    public void queueUpdateAll(Boolean force) {

        for (AnimeStatus animeStatus : AnimeStatus.getDisplayable()) {
            this.queueUpdate(animeStatus, force);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Anime Night">

    public boolean canSchedule(ZonedDateTime time, long amount) {

        List<AnimeNight> animeNights = this.animeNightRepository.findAllByStatusIn(Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED));
        long             minuteWatch = DiscordUtils.getNearest(amount * 20 + 3, 5); // 20m per episode + 3 minutes of op&ed (1m30 each)
        OffsetDateTime   startTime   = time.toOffsetDateTime();
        OffsetDateTime   endTime     = startTime.plusMinutes(minuteWatch);

        return animeNights.stream()
                          .noneMatch(night -> AnimeNights.isOverlapping(night, startTime, endTime));
    }

    public void recalibrateSchedule(Anime anime) {

        JDA   instance = this.store.requireInstance();
        Guild guild    = instance.getGuildById(this.toshikoAnimeServer);

        if (guild == null) {
            throw new JdaUnavailableException();
        }

        List<AnimeNight> allAnimeNights = this.animeNightRepository.findAllByStatusIn(Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED));
        allAnimeNights.sort(Comparator.comparing(AnimeNight::getStartTime));

        Map<Anime, List<AnimeNight>> scheduledByAnime = MapUtils.groupBy(allAnimeNights, AnimeNight::getAnime);

        List<AnimeNight> schedule = scheduledByAnime.getOrDefault(anime, Collections.emptyList());

        long effectiveWatched = anime.getWatched();

        for (AnimeNight animeNight : schedule) {
            // Description update ! - Update the description to match episode numbers (in case of unordered scheduling)
            this.publisher.publishEvent(new AnimeNightUpdateEvent(this, guild, animeNight, effectiveWatched));
            effectiveWatched += animeNight.getAmount();
        }
    }

    public AnimeNight schedule(Anime anime, ZonedDateTime time, long amount) {

        JDA   instance = this.store.requireInstance();
        Guild guild    = instance.getGuildById(this.toshikoAnimeServer);

        if (guild == null) {
            throw new JdaUnavailableException();
        }

        Animes.requireValidProgression(anime, amount);
        List<AnimeNight> animeNights = this.animeNightRepository.findAllByStatusIn(Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED));


        long totalOpEdTimeSkip = (amount - 1) * 3; // Only watch one op & ed.
        long totalDuration     = amount * anime.getEpisodeDuration();

        long           minuteWatch = DiscordUtils.getNearest(totalDuration - totalOpEdTimeSkip, 5);
        OffsetDateTime startTime   = time.toOffsetDateTime();
        OffsetDateTime endTime     = startTime.plusMinutes(minuteWatch);

        animeNights.sort(Comparator.comparing(AnimeNight::getStartTime));

        long lastWatched      = anime.getWatched();
        long effectiveWatched = anime.getWatched();
        for (AnimeNight animeNight : animeNights) {
            if (animeNight.getAnime().equals(anime)) {

                if (startTime.isAfter(animeNight.getStartTime())) {
                    lastWatched += animeNight.getAmount();
                    effectiveWatched += animeNight.getAmount();
                } else {
                    // Description update ! - Update the description to match episode numbers (in case of unordered scheduling)
                    lastWatched += animeNight.getAmount();
                    this.publisher.publishEvent(new AnimeNightUpdateEvent(this, guild, animeNight, lastWatched));
                }
            }
        }

        LOGGER.info("START: {}, END: {}", startTime, endTime);

        ScheduledEventAction action = guild.createScheduledEvent(anime.getName(), "Discord", startTime, endTime)
                                           .setDescription(AnimeNights.createDescription(amount, effectiveWatched));

        IRestAction<byte[]> image = new FileDownloader(String.format("https://toshiko.alexpado.fr/%s.png", anime.getId()));
        ScheduledEvent      scheduledEvent;
        try {
            byte[] imgData = image.complete();
            scheduledEvent = action.setImage(Icon.from(imgData)).complete();
        } catch (Exception e) {
            Sentry.captureException(e);
            scheduledEvent = action.complete();
        }

        AnimeNight event = new AnimeNight(scheduledEvent, anime, amount);
        return this.animeNightRepository.save(event);
    }

    public Optional<AnimeNight> updateEvent(ScheduledEvent event, Consumer<AnimeNight> updater) {

        return this.animeNightRepository.findById(event.getIdLong())
                                        .map(animeNight -> {
                                            updater.accept(animeNight);
                                            return this.animeNightRepository.save(animeNight);
                                        });
    }

    public Optional<AnimeNight> changeEventStatus(ScheduledEvent event, Consumer<AnimeNight> modifier) {

        return this.updateEvent(event, night -> {
            night.setStatus(event.getStatus());
            modifier.accept(night);
        });
    }

    public Optional<AnimeNight> startEvent(ScheduledEvent event) {

        return this.changeEventStatus(event, night -> {
            Anime anime = night.getAnime();
            this.setAnimeProgression(anime, anime.getWatched());
        });
    }

    public Optional<AnimeNight> closeEvent(ScheduledEvent event) {

        return this.changeEventStatus(event, night -> {
            Anime anime = night.getAnime();
            this.setAnimeProgression(anime, anime.getWatched() + night.getAmount());
        });
    }
    // </editor-fold>
}
