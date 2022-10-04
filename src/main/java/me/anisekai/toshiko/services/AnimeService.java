package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.entities.keys.InterestKey;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.AnimeUpdateType;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.AnimeUpdateEvent;
import me.anisekai.toshiko.events.WatchlistUpdatedEvent;
import me.anisekai.toshiko.exceptions.animes.AnimeAlreadyRegisteredException;
import me.anisekai.toshiko.exceptions.animes.AnimeNotFoundException;
import me.anisekai.toshiko.exceptions.animes.InvalidAnimeProgressException;
import me.anisekai.toshiko.exceptions.animes.InvalidAnimeStatusException;
import me.anisekai.toshiko.exceptions.interests.InterestLevelUnchangedException;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.repositories.InterestRepository;
import me.anisekai.toshiko.tasks.AnimeTask;
import net.dv8tion.jda.api.entities.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnimeService {

    private final ApplicationEventPublisher publisher;
    private final UserService               userService;
    private final AnimeRepository           repository;
    private final InterestRepository        interestRepository;

    public AnimeService(ApplicationEventPublisher publisher, UserService userService, AnimeRepository repository, InterestRepository interestRepository) {

        this.publisher          = publisher;
        this.userService        = userService;
        this.repository         = repository;
        this.interestRepository = interestRepository;
    }

    /**
     * Retrieve the count of displayable {@link Anime} in the database.
     *
     * @return The count of displayable {@link AnimeTask}.
     */
    public long getDisplayableCount() {

        return this.repository.findAllByStatusIn(AnimeStatus.getDisplayable()).size();
    }

    /**
     * Retrieve all {@link Anime} having the provided status.
     *
     * @param status
     *         An {@link AnimeStatus}
     *
     * @return A sorted {@link List} of {@link Anime}.
     */
    public List<Anime> findAllByStatus(AnimeStatus status) {

        return this.repository.findAllByStatus(status).stream().sorted().toList();
    }

    public Anime createFromProvider(User user, AnimeProvider provider) {
        return this.createFromProvider(user, provider, provider.getPublicationState().getStatus());
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
    public Anime createFromProvider(User user, AnimeProvider provider, AnimeStatus status) {

        Optional<Anime> optionalAnime = this.repository.findByName(provider.getName());

        if (optionalAnime.isPresent()) {
            throw new AnimeAlreadyRegisteredException(provider.getName());
        }

        DiscordUser discordUser = this.userService.retrieve(user);
        Anime       anime       = new Anime(discordUser, provider, status);

        try {
            anime = this.repository.save(anime);
            Interest interest = new Interest(anime, discordUser, InterestLevel.INTERESTED);
            this.interestRepository.save(interest);
            return anime;
        } finally {
            this.publisher.publishEvent(new WatchlistUpdatedEvent(this, anime.getStatus()));
            this.publisher.publishEvent(new AnimeUpdateEvent(this, anime, AnimeUpdateType.ADDED));
        }
    }

    /**
     * Find an {@link Anime} by its id.
     *
     * @param id
     *         The {@link Anime}'s id.
     *
     * @return An {@link Anime}.
     */
    public Anime findById(long id) {

        return this.repository.findById(id).orElseThrow(AnimeNotFoundException::new);
    }

    /**
     * Change the {@link Anime} matching the provided {@link AnimeStatus} to the provided {@link AnimeStatus}. Nothing
     * will happen if the {@link AnimeStatus} of the {@link Anime} is the same as the one provided to avoid useless
     * message update and database query.
     *
     * @param id
     *         The {@link Anime}'s id.
     * @param status
     *         The new {@link AnimeStatus}.
     */
    public void swapAnimeStatus(long id, AnimeStatus status) {

        Anime anime = this.findById(id);

        if (anime.getStatus() == status) {
            return;
        }

        WatchlistUpdatedEvent oldWatchlist = new WatchlistUpdatedEvent(this, anime.getStatus());
        WatchlistUpdatedEvent newWatchlist = new WatchlistUpdatedEvent(this, status);

        anime.setStatus(status);

        if (status == AnimeStatus.WATCHED) {
            anime.setWatched(anime.getWatched());
        }

        this.repository.save(anime);

        this.publisher.publishEvent(oldWatchlist);
        this.publisher.publishEvent(newWatchlist);

        this.publisher.publishEvent(new AnimeUpdateEvent(this, anime, AnimeUpdateType.UPDATE));
    }

    /**
     * Define the watching progress on the {@link Anime} matching the provided id
     *
     * @param id
     *         The {@link Anime}'s id
     * @param watched
     *         The progress made
     */
    public boolean setAnimeProgress(long id, long watched, Long amount) {

        Anime anime = this.findById(id);
        if (anime.getStatus() != AnimeStatus.WATCHING && anime.getStatus() != AnimeStatus.SIMULCAST) {
            throw new InvalidAnimeStatusException();
        }

        if (amount != null) {
            anime.setTotal(amount);
        }

        if (anime.getTotal() < watched || watched < 0) {
            throw new InvalidAnimeProgressException();
        }
        anime.setWatched(watched);
        this.repository.save(anime);

        if (anime.getWatched() == anime.getTotal()) {
            this.swapAnimeStatus(anime.getId(), AnimeStatus.WATCHED);
            return true;
        } else {
            this.publisher.publishEvent(new WatchlistUpdatedEvent(this, anime.getStatus()));
        }

        return false;
    }

    /**
     * Change the {@link InterestLevel} of a {@link User} for the {@link Anime} matching the provided id.
     *
     * @param target
     *         The {@link User} from which the {@link InterestLevel} will be changed
     * @param id
     *         The {@link Anime}'s id
     * @param level
     *         The new {@link InterestLevel}.
     */
    public Interest swapAnimeInterest(User target, long id, InterestLevel level) {

        Anime       anime = this.findById(id);
        DiscordUser user  = this.userService.retrieve(target);

        Optional<Interest> optionalInterest = this.interestRepository.findById(new InterestKey(anime, user));
        Interest           interest;
        if (optionalInterest.isPresent()) {
            interest = optionalInterest.get();
            if (interest.getLevel() == level) {
                throw new InterestLevelUnchangedException(interest);
            }
            interest.setLevel(level);
            this.interestRepository.save(interest);
            this.publisher.publishEvent(new WatchlistUpdatedEvent(this, anime.getStatus()));
        } else {
            interest = new Interest(anime, user, level);
            this.interestRepository.save(interest);

            if (level != InterestLevel.NEUTRAL) {
                this.publisher.publishEvent(new WatchlistUpdatedEvent(this, anime.getStatus()));
            }
        }

        this.publisher.publishEvent(new AnimeUpdateEvent(this, anime, AnimeUpdateType.UPDATE));
        return interest;
    }

    /**
     * Retrieve all {@link Interest} record for the provided {@link Anime}.
     *
     * @param anime
     *         The {@link Anime} from which the {@link Interest}s should be fetched.
     *
     * @return A {@link List} of {@link Anime}.
     */
    public List<Interest> getInterests(Anime anime) {

        return this.interestRepository.findAllActiveByAnime(anime);
    }

    /**
     * Retrieve all {@link Anime} that should be checked regularly for a status update.
     *
     * @return A {@link List} of {@link Anime}.
     */
    public List<Anime> getUpdatableAnime() {

        return this.repository.findAllByStatusIn(Arrays.asList(AnimeStatus.UNAVAILABLE, AnimeStatus.SIMULCAST_AVAILABLE, AnimeStatus.SIMULCAST));
    }

    /**
     * Retrieve all vote score on {@link Anime} that support votes.
     *
     * @return A {@link Map} associating each {@link Anime} to their vote score.
     */
    public Map<Anime, Double> getAnimeVotes() {

        Map<DiscordUser, Double> power = new HashMap<>();
        Map<Anime, Double>       votes = new HashMap<>();

        List<Interest> interests = this.interestRepository.findAllActive()
                                                          .stream()
                                                          .filter(vote -> vote.getLevel() != InterestLevel.NEUTRAL)
                                                          .toList();

        List<DiscordUser> users = interests.stream()
                                           .map(Interest::getUser)
                                           .distinct()
                                           .toList();

        List<Anime> animes = interests.stream()
                                      .map(Interest::getAnime)
                                      .filter(anime -> anime.getStatus().isWatchable())
                                      .distinct()
                                      .toList();


        long nonNeutralVote = interests.size();

        for (DiscordUser user : users) {
            long nonNeutralUserVote = interests.stream()
                                               .filter(vote -> vote.getUser().equals(user))
                                               .count();

            power.put(user, (double) nonNeutralUserVote / (double) nonNeutralVote);
        }

        for (Anime anime : animes) {

            double votePower = interests.stream()
                                        .filter(vote -> vote.getAnime().equals(anime))
                                        .mapToDouble(vote -> power.getOrDefault(vote.getUser(), 0.0) * vote.getLevel()
                                                                                                           .getPowerModifier())
                                        .sum();

            votes.put(anime, votePower);
        }

        return votes;
    }

    /**
     * Ask the task to refresh every watchlist
     */
    public void refreshWatchlist() {

        for (AnimeStatus status : AnimeStatus.getDisplayable()) {
            this.publisher.publishEvent(new WatchlistUpdatedEvent(this, status));
        }
    }

}
