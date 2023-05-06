package me.anisekai.toshiko.helpers;

import me.anisekai.toshiko.data.BookedAnimeNight;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.exceptions.nights.AnimeNightOverlappingException;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class AnimeNightScheduler<T extends AnimeNightMeta> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimeNightScheduler.class);

    private final Set<T> nights;

    public AnimeNightScheduler(Collection<T> sourceData) {

        this.nights = new HashSet<>(sourceData);
    }

    /**
     * Find an optional {@link T} for the provided anime having its {@link OffsetDateTime} closest, but not greater than
     * the provided {@link OffsetDateTime}.
     *
     * @param anime
     *         The {@link Anime} used to filter {@link T}
     * @param before
     *         The {@link OffsetDateTime} that {@link T} should not exceed.
     *
     * @return An instance of {@link T}, if any matched.
     */
    public Optional<T> findLatestFor(Anime anime, OffsetDateTime before) {

        return this.nights.stream()
                          .filter(night -> night.getAnime().equals(anime))
                          .filter(night -> night.getStartDateTime().isBefore(before))
                          .max(Comparator.comparing(AnimeNightMeta::getStartDateTime));
    }

    /**
     * Find an optional {@link T} for the provided anime having its {@link OffsetDateTime} closest, but not less than
     * the provided {@link OffsetDateTime}.
     *
     * @param anime
     *         The {@link Anime} used to filter {@link T}
     * @param after
     *         The {@link OffsetDateTime} that {@link T} should not be under.
     *
     * @return An instance of {@link T}, if any matched.
     */
    public Optional<T> findFirstFor(Anime anime, OffsetDateTime after) {

        return this.nights.stream()
                          .filter(night -> night.getAnime().equals(anime))
                          .filter(night -> night.getStartDateTime().isAfter(after))
                          .min(Comparator.comparing(AnimeNightMeta::getStartDateTime));
    }

    /**
     * Schedule a new event with the provided data.
     *
     * @param anime
     *         The {@link Anime} for which the event will be scheduled.
     * @param amount
     *         The amount of episode that should be scheduled.
     * @param at
     *         The {@link ZonedDateTime} at which the event should be scheduled.
     * @param scheduler
     *         {@link Function} converting a {@link BookedAnimeNight} holding the pre-scheduling data to the event
     *         entity {@link T}.
     *
     * @return The scheduled event {@link T}, if scheduled successfully.
     */
    public T scheduleAt(Anime anime, long amount, ZonedDateTime at, Function<BookedAnimeNight, T> scheduler) {

        LOGGER.info("Trying to schedule {} episode of anime {} at {}", amount, anime.getId(), at);

        long duration = anime.getEpisodeDuration() * amount - (3 * (amount - 1));

        OffsetDateTime startingAt = at.toOffsetDateTime();
        OffsetDateTime endingAt   = startingAt.plusMinutes(duration);

        // Check for collision
        if (this.nights.stream().anyMatch(night -> night.isColliding(startingAt, endingAt))) {
            LOGGER.warn("Could not schedule at {}: Collision detected.", at);
            throw new AnimeNightOverlappingException(anime, startingAt, endingAt);
        }

        // No collision !
        Optional<? extends AnimeNightMeta> optionalLatest = this.findLatestFor(anime, startingAt);

        long firstEpisode, lastEpisode;

        // Do we have a previous event ?
        if (optionalLatest.isPresent()) {
            LOGGER.debug("Previous scheduled event detected. Setting watch data based on latest occurrence...");
            AnimeNightMeta meta = optionalLatest.get();
            firstEpisode = meta.getLastEpisode() + 1;
            lastEpisode  = meta.getLastEpisode() + amount;
        } else {
            LOGGER.debug("No previous scheduled event detected. Setting watch data based on anime data....");
            firstEpisode = anime.getWatched() + 1;
            lastEpisode  = anime.getWatched() + amount;
        }

        BookedAnimeNight night = new BookedAnimeNight(anime, firstEpisode, lastEpisode, amount, startingAt, endingAt);

        LOGGER.debug("Calling scheduler function...");
        T instance = scheduler.apply(night);

        LOGGER.info("Event scheduled !");
        this.nights.add(instance);

        return instance;
    }

    /**
     * Schedule multiple event using the provided data until there is nothing to schedule anymore for the provided
     * {@link Anime}.
     *
     * @param anime
     *         The {@link Anime} for which the event will be scheduled.
     * @param amount
     *         The amount of episode that should be scheduled.
     * @param startingAt
     *         The {@link ZonedDateTime} at which the group scheduling will start.
     * @param scheduler
     *         {@link Function} converting a {@link BookedAnimeNight} holding the pre-scheduling data to the event
     *         entity {@link T}.
     * @param incrementalTime
     *         {@link Function} used to determine how time should be skipped between two scheduled events. This can be
     *         used to create weekly event for example.
     */
    public void scheduleAllStartingAt(Anime anime, long amount, ZonedDateTime startingAt, Function<BookedAnimeNight, T> scheduler, Function<ZonedDateTime, ZonedDateTime> incrementalTime) {

        LOGGER.info("Trying to schedule Anime {} completely...", anime.getId());

        if (anime.getTotal() < 1) {
            LOGGER.warn("Unknown total of episode: Can't schedule.");
            throw new IllegalStateException("You can't schedule every episode of this anime: The total number of episode is unknown.");
        }

        ZonedDateTime now           = ZonedDateTime.now();
        ZonedDateTime securityLimit = now.plusYears(2);
        ZonedDateTime scheduleAt    = startingAt.withSecond(0).withNano(0);

        if (scheduleAt.isBefore(now)) {
            LOGGER.warn("Tried to schedule in the past.");
            throw new IllegalStateException("Are you fucking kidding me ? Don't schedule thing in the past you moron");
        }

        Optional<T> optionalLatest = this.findLatestFor(anime, scheduleAt.toOffsetDateTime());
        long left = anime.getTotal() - optionalLatest.map(AnimeNightMeta::getLastEpisode)
                                                     .orElseGet(anime::getWatched);

        while (left > 0) {
            LOGGER.debug("Watch data: {} episodes left to schedule.", left);
            long nextAmount = Math.min(amount, left);

            try {
                LOGGER.debug("Trying to schedule {} episodes at {}", nextAmount, scheduleAt);
                this.scheduleAt(anime, nextAmount, scheduleAt, scheduler);
                LOGGER.info("An event has been scheduled at {} !", scheduleAt);
                left -= nextAmount;
            } catch (AnimeNightOverlappingException ignore) {
                LOGGER.debug("Could not schedule at {}: Something is already scheduled here.", scheduleAt);
                // Expected exception, let's continue.
            }

            scheduleAt = incrementalTime.apply(scheduleAt);

            if (scheduleAt.isAfter(securityLimit)) {
                LOGGER.error("WOW ! Tried to schedule an event 2 years later, something is obviously wrong here.");
                throw new IllegalStateException("Could not completely schedule the anime within a 2 years time period. Either you have a lot to watch, or you fucked up the code.");
            }
        }
    }

    /**
     * Calibrate the schedule. Calibrating the schedule will allow updating the episode of the scheduled event and
     * correct any kind of time delay due to the episode update if necessary.
     * <p>
     * This is a simple syntax sugar for {@link #calibrate(Iterable, Consumer)}.
     *
     * @param anime
     *         The {@link Anime} for which the calibrating should be done.
     * @param onUpdate
     *         {@link Consumer} called when the entity has been calibrated.
     *
     * @see #calibrate(Iterable, Consumer)
     */
    public void calibrate(Anime anime, Consumer<T> onUpdate) {

        this.calibrate(Collections.singleton(anime), onUpdate);
    }

    /**
     * Calibrate the schedule. Calibrating the schedule will allow updating the episode of the scheduled event and
     * correct any kind of time delay due to the episode update if necessary.
     *
     * @param animes
     *         The {@link Anime} collection for which the calibrating should be done.
     * @param onUpdate
     *         {@link Consumer} called when the entity has been calibrated.
     */
    public void calibrate(Iterable<Anime> animes, Consumer<T> onUpdate) {

        for (Anime anime : animes) {
            LOGGER.info("Calibrating anime {}...", anime.getId());
            // Even if the ZonID is wrong there, it's almost impossible that it is before "now"... right ?
            // ... What are you doing with this time travel machine ? Put it away, I beg you.
            OffsetDateTime time = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()).toOffsetDateTime();

            Optional<T> optionalFirst = this.findFirstFor(anime, time);
            long        watched       = anime.getWatched();

            while (optionalFirst.isPresent()) {
                T instance = optionalFirst.get();

                long correctFirstEpisode = watched + 1;
                long correctLastEpisode  = watched + instance.getAmount();
                long correctAmount       = instance.getAmount();

                if (correctLastEpisode > anime.getTotal()) {
                    // Erm... *ahem* why not following the fucking schedule, huh ?
                    correctLastEpisode = anime.getTotal();
                    correctAmount      = correctLastEpisode - correctFirstEpisode + 1;
                }

                boolean hasBeenUpdated = false;

                if (instance.getFirstEpisode() != correctFirstEpisode) {
                    long delta = correctFirstEpisode - instance.getFirstEpisode();
                    instance.setFirstEpisode(instance.getFirstEpisode() + delta);
                    hasBeenUpdated = true;
                }

                if (instance.getLastEpisode() != correctLastEpisode) {
                    long delta = correctLastEpisode - instance.getLastEpisode();
                    instance.setLastEpisode(instance.getLastEpisode() + delta);
                    hasBeenUpdated = true;
                }

                if (instance.getAmount() != correctAmount) {
                    // We wouldn't be there if you followed the schedule... Yeah, I already said that line 178...
                    instance.setLastEpisode(instance.getFirstEpisode() + correctAmount - 1);
                    long duration = anime.getEpisodeDuration() * correctAmount - (3 * (correctAmount - 1));
                    instance.setEndDateTime(instance.getStartDateTime().plusMinutes(duration));
                    hasBeenUpdated = true;
                }

                if (hasBeenUpdated) {
                    // Notify the caller that people are dumb fucks that can't decide when to watch something.
                    onUpdate.accept(instance);
                }

                watched       = instance.getLastEpisode();
                time          = instance.getEndDateTime();
                optionalFirst = this.findFirstFor(anime, time);
            }
        }
    }

    public void delay(long value, TimeUnit unit, Predicate<OffsetDateTime> predicate, Consumer<T> onUpdate) {

        LOGGER.info("Trying to delay schedule by {} {}", value, unit.name());
        // Retrieve delayable entities.
        List<T> delayable = this.nights.stream()
                                       .filter(event -> predicate.test(event.getStartDateTime()))
                                       .sorted(Comparator.comparing(AnimeNightMeta::getStartDateTime).reversed())
                                       .toList();

        // We first check if we can delay safely.
        LOGGER.info("Checking if schedule can be delayed...");
        for (T event : delayable) {
            // Make a copy
            BookedAnimeNight night = new BookedAnimeNight(event);

            // Apply delay
            night.setStartDateTime(night.getStartDateTime().plus(value, unit.toChronoUnit()));

            // Check if schedulable (ignoring the delayable events)...
            boolean collide = this.nights.stream()
                                         .filter(item -> !delayable.contains(item))
                                         .anyMatch(item -> item.isColliding(night));


            if (collide) {
                LOGGER.info("Nope, it can't be scheduled.");
                throw new AnimeNightOverlappingException(night.getAnime(), night.getStartDateTime(), night.getEndDateTime());
            }
        }

        LOGGER.info("Delaying...");
        // Hey, we can delay the schedule !
        for (T event : delayable) {
            // Apply delay
            event.setStartDateTime(event.getStartDateTime().plus(value, unit.toChronoUnit()));
            onUpdate.accept(event);
        }
    }
}
