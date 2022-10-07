package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.*;
import me.anisekai.toshiko.enums.*;
import me.anisekai.toshiko.events.AnimeUpdateEvent;
import me.anisekai.toshiko.exceptions.animes.AnimeAlreadyRegisteredException;
import me.anisekai.toshiko.exceptions.animes.AnimeNotFoundException;
import me.anisekai.toshiko.exceptions.interests.InterestLevelUnchangedException;
import me.anisekai.toshiko.exceptions.users.EmojiAlreadyUsedException;
import me.anisekai.toshiko.exceptions.users.InvalidEmojiException;
import me.anisekai.toshiko.helpers.containers.InterestPower;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.repositories.*;
import me.anisekai.toshiko.utils.MapUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class ToshikoService {

    private final ApplicationEventPublisher publisher;
    private final AnimeRepository           animeRepository;
    private final UserRepository            userRepository;
    private final WatchlistRepository       watchlistRepository;
    private final InterestRepository        interestRepository;
    private final ScheduledEventRepository  scheduledEventRepository;

    public ToshikoService(ApplicationEventPublisher publisher, AnimeRepository animeRepository, UserRepository userRepository, WatchlistRepository watchlistRepository, InterestRepository interestRepository, ScheduledEventRepository scheduledEventRepository) {

        this.publisher                = publisher;
        this.animeRepository          = animeRepository;
        this.userRepository           = userRepository;
        this.watchlistRepository      = watchlistRepository;
        this.interestRepository       = interestRepository;
        this.scheduledEventRepository = scheduledEventRepository;
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

    public ScheduledEventRepository getScheduledEventRepository() {

        return this.scheduledEventRepository;
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
            this.queueUpdate(anime.getStatus());
        }

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
        this.publisher.publishEvent(new AnimeUpdateEvent(this, anime, AnimeUpdateType.ADDED));
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
        return this.interestRepository.save(interest);
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

        if (!AnimeStatus.getDisplayable().contains(status)) {
            return;
        }

        Optional<Watchlist> optionalWatchlist = this.watchlistRepository.findById(status);

        if (optionalWatchlist.isEmpty()) {
            return;
        }

        Watchlist watchlist = optionalWatchlist.get();
        watchlist.setState(CronState.REQUIRED);
        this.watchlistRepository.save(watchlist);
    }

    public void queueUpdateAll() {

        for (AnimeStatus animeStatus : AnimeStatus.getDisplayable()) {
            this.queueUpdate(animeStatus);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Scheduled Events">

    public List<ScheduledEvent> getNonNotifiedEvents() {

        return this.scheduledEventRepository.findAll()
                                            .stream()
                                            .filter(event -> event.getEventStartAt().isAfter(LocalDateTime.now()))
                                            .filter(event -> event.getState() == ScheduledEventState.SCHEDULED)
                                            .filter(event -> !event.isNotified())
                                            .toList();
    }

    public Optional<ScheduledEvent> setEventState(int id, ScheduledEventState state) {

        return this.setEventState(id, state, (ev) -> {});
    }

    public Optional<ScheduledEvent> setEventState(int id, ScheduledEventState state, Consumer<ScheduledEvent> beforeSave) {

        Optional<ScheduledEvent> optionalEvent = this.scheduledEventRepository.findById(id);

        if (optionalEvent.isEmpty()) {
            return optionalEvent;
        }

        ScheduledEvent scheduledEvent = optionalEvent.get();
        scheduledEvent.setState(state);
        beforeSave.accept(scheduledEvent);
        this.scheduledEventRepository.save(scheduledEvent);
        return Optional.of(scheduledEvent);
    }

    public Optional<ScheduledEvent> cancelEvent(int id) {

        return this.setEventState(id, ScheduledEventState.CANCELLED);
    }

    public Optional<ScheduledEvent> startEvent(int id) {

        return this.setEventState(id, ScheduledEventState.OPENED);
    }

    public Optional<ScheduledEvent> finishEvent(int id) {

        return this.setEventState(id, ScheduledEventState.FINISHED, event -> {
            Anime anime    = event.getAnime();
            int   progress = event.getLastEpisode();
            this.setAnimeProgression(anime, progress);
        });
    }

    public void setNotified(ScheduledEvent event) {

        event.setNotified(true);
        this.scheduledEventRepository.save(event);
    }
    // </editor-fold>
}
