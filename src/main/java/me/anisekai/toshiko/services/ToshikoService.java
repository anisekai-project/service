package me.anisekai.toshiko.services;

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
import me.anisekai.toshiko.exceptions.nights.OverlappingScheduleException;
import me.anisekai.toshiko.exceptions.users.EmojiAlreadyUsedException;
import me.anisekai.toshiko.exceptions.users.InvalidEmojiException;
import me.anisekai.toshiko.helpers.AnimeNightScheduler;
import me.anisekai.toshiko.helpers.JdaStoreService;
import me.anisekai.toshiko.helpers.containers.InterestPower;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.repositories.*;
import me.anisekai.toshiko.utils.MapUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class ToshikoService {

    private static final Logger                      LOGGER          = LoggerFactory.getLogger(ToshikoService.class);
    private static final List<ScheduledEvent.Status> ACTIVE_STATUSES = Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED);

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

    // <editor-fold desc="Reloaders">
    public Anime reload(Anime anime) {

        return this.animeRepository.findById(anime.getId())
                                   .orElseThrow(() -> new IllegalStateException("Could not reload anime entity."));
    }

    public AnimeNight reload(AnimeNight animeNight) {

        return this.animeNightRepository.findById(animeNight.getId())
                                        .orElseThrow(() -> new IllegalStateException("Could not reload animeNight entity."));
    }

    public Watchlist reload(Watchlist watchlist) {

        return this.watchlistRepository.findById(watchlist.getStatus())
                                       .orElseThrow(() -> new IllegalStateException("Could not reload watchlist entity."));
    }
    // </editor-fold>

    // <editor-fold desc="Components, Service & Repositories">
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

    public ApplicationEventPublisher getPublisher() {

        return this.publisher;
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
        Anime save = this.animeRepository.save(anime);
        this.refreshAnimeAnnounce(save);
        return this.animeRepository.save(save);
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
        this.publisher.publishEvent(new AnimeUpdateEvent(this, reloaded, AnimeUpdateType.ADDED));
    }

    /**
     * Send an event for the provided {@link Anime}
     *
     * @param anime
     *         The {@link Anime} source of the notification.
     */
    public void refreshAnimeAnnounce(Anime anime) {

        Anime reloaded = this.findAnime(anime.getId());

        this.publisher.publishEvent(switch (reloaded.getStatus()) {
            case WATCHED -> new AnimeUpdateEvent(this, reloaded, AnimeUpdateType.REMOVE);
            case WATCHING,
                    UNAVAILABLE,
                    NO_SOURCE,
                    NOT_DOWNLOADED,
                    DOWNLOADING,
                    DOWNLOADED,
                    SIMULCAST_AVAILABLE,
                    SIMULCAST -> new AnimeUpdateEvent(this, reloaded, AnimeUpdateType.UPDATE);
        });
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
                                     .mapToDouble(interest -> interest.getValue(power) * 100)
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
    public AnimeNightScheduler<AnimeNight> createScheduler() {

        return new AnimeNightScheduler<>(this.animeNightRepository.findAllByStatusIn(AnimeNight.WATCHABLE));
    }

    public void refreshSchedule() {

        this.animeNightRepository.findAllByStatusIn(AnimeNight.WATCHABLE).stream()
                                 .map(night -> new AnimeNightUpdateEvent(this, this.getBotGuild(), night))
                                 .forEach(this.getPublisher()::publishEvent);
    }

    public Optional<AnimeNight> findAnimeNight(ScheduledEvent event) {

        return this.animeNightRepository.findByEventId(event.getIdLong());
    }

    public AnimeNight schedule(Anime anime, ZonedDateTime time, long amount) {

        AnimeNightScheduler<AnimeNight> scheduler          = this.createScheduler();
        Optional<AnimeNight>            optionalAnimeNight = scheduler.scheduleAt(anime, amount, time, booking -> this.animeNightRepository.save(new AnimeNight(booking)));

        if (optionalAnimeNight.isPresent()) {
            AnimeNightUpdateEvent event = new AnimeNightUpdateEvent(this, this.getBotGuild(), optionalAnimeNight.get());
            this.getPublisher().publishEvent(event);
            return optionalAnimeNight.get();
        }

        throw new OverlappingScheduleException(anime);
    }

    public List<AnimeNight> scheduleAll(Anime anime, ZonedDateTime scheduleAt, long amount, Function<ZonedDateTime, ZonedDateTime> timeIncrement) {

        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        // Normalize date
        ZonedDateTime scheduleFrom = scheduleAt.withSecond(0).withNano(0);

        List<AnimeNight> nights = new ArrayList<>();
        scheduler.scheduleAllStartingAt(anime, amount, scheduleFrom, booking -> {
            AnimeNight night = this.animeNightRepository.save(new AnimeNight(booking));
            nights.add(night);
            return night;
        }, timeIncrement);

        nights.stream()
              .map(night -> new AnimeNightUpdateEvent(this, this.getBotGuild(), night))
              .forEach(this.getPublisher()::publishEvent);

        return nights;
    }

    public int calibrate() {

        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        Set<AnimeNight> updated = new HashSet<>();
        scheduler.calibrate(this.animeRepository.findAll(), updated::add);

        this.animeNightRepository.saveAll(updated).stream()
                                 .map(night -> new AnimeNightUpdateEvent(this, this.getBotGuild(), night))
                                 .forEach(this.getPublisher()::publishEvent);

        return updated.size();
    }

    public int calibrate(Anime anime) {

        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        Set<AnimeNight> updated = new HashSet<>();
        scheduler.calibrate(anime, updated::add);

        this.animeNightRepository.saveAll(updated).stream()
                                 .map(night -> new AnimeNightUpdateEvent(this, this.getBotGuild(), night))
                                 .forEach(this.getPublisher()::publishEvent);

        return updated.size();
    }

    public int delay(long minutes) {

        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        // time limit
        OffsetDateTime limit = ZonedDateTime.now().plusHours(6).toOffsetDateTime();

        Set<AnimeNight> updated = new HashSet<>();
        if (!scheduler.delay(minutes, TimeUnit.MINUTES, time -> time.isBefore(limit), updated::add)) {
            return -1;
        }

        this.animeNightRepository.saveAll(updated).stream()
                                 .map(night -> new AnimeNightUpdateEvent(this, this.getBotGuild(), night))
                                 .forEach(this.getPublisher()::publishEvent);

        return updated.size();
    }

    public void cancelEvent(ScheduledEvent event) {

        this.findAnimeNight(event).ifPresent(night -> {
            this.animeNightRepository.deleteById(night.getId());
            AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

            Set<AnimeNight> updated = new HashSet<>();
            scheduler.calibrate(night.getAnime(), updated::add);

            this.animeNightRepository.saveAll(updated);
            updated.forEach(calibrated -> {
                this.publisher.publishEvent(new AnimeNightUpdateEvent(this, event.getGuild(), calibrated));
            });
        });

    }

    public void closeEvent(ScheduledEvent event) {

        this.findAnimeNight(event).ifPresent(night -> {
            this.setAnimeProgression(night.getAnime(), night.getLastEpisode());
            night.setStatus(ScheduledEvent.Status.COMPLETED);
            this.animeNightRepository.save(night);
        });
    }

    public void startEvent(ScheduledEvent event) {

        this.findAnimeNight(event).ifPresent(night -> {
            switch (night.getAnime().getStatus()) {
                case SIMULCAST_AVAILABLE -> this.setAnimeStatus(night.getAnime(), AnimeStatus.SIMULCAST);
                case DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED, NO_SOURCE -> this.setAnimeStatus(night.getAnime(), AnimeStatus.WATCHING);
            }
        });
    }
    // </editor-fold>


    public Guild getBotGuild() {

        JDA   instance = this.store.requireInstance();
        Guild guild    = instance.getGuildById(this.toshikoAnimeServer);

        if (guild == null) {
            throw new JdaUnavailableException();
        }

        return guild;
    }
}
