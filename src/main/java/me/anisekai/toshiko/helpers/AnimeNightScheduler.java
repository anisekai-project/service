package me.anisekai.toshiko.helpers;

import me.anisekai.toshiko.data.BookedAnimeNight;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import net.dv8tion.jda.api.entities.ScheduledEvent;
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

    private static final Logger                      LOGGER   = LoggerFactory.getLogger(AnimeNightScheduler.class);
    public final static  List<ScheduledEvent.Status> STATUSES = Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED);

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
    public Optional<T> scheduleAt(Anime anime, long amount, ZonedDateTime at, Function<BookedAnimeNight, T> scheduler) {

        long duration = anime.getEpisodeDuration() * amount - (3 * (amount - 1));

        OffsetDateTime startingAt = at.toOffsetDateTime();
        OffsetDateTime endingAt   = startingAt.plusMinutes(duration);

        // Check for collision
        if (this.nights.stream().anyMatch(night -> night.isColliding(startingAt, endingAt))) {
            return Optional.empty();
        }

        // No collision !
        Optional<? extends AnimeNightMeta> optionalLatest = this.findLatestFor(anime, startingAt);

        long firstEpisode, lastEpisode;

        // Do we have a previous event ?
        if (optionalLatest.isPresent()) {
            AnimeNightMeta meta = optionalLatest.get();
            firstEpisode = meta.getLastEpisode() + 1;
            lastEpisode  = meta.getLastEpisode() + amount;
        } else {
            firstEpisode = anime.getWatched() + 1;
            lastEpisode  = anime.getWatched() + amount;
        }

        BookedAnimeNight night    = new BookedAnimeNight(anime, firstEpisode, lastEpisode, amount, startingAt, endingAt);
        T                instance = scheduler.apply(night);
        this.nights.add(instance);

        return Optional.of(instance);
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

        if (anime.getTotal() < 1) {
            throw new IllegalStateException("You can't schedule every episode of this anime: The total number of episode is unknown.");
        }

        ZonedDateTime now           = ZonedDateTime.now();
        ZonedDateTime securityLimit = now.plusYears(2);
        ZonedDateTime scheduleAt    = startingAt;

        if (scheduleAt.isBefore(now)) {
            throw new IllegalStateException("Are you fucking kidding me ? Don't schedule thing in the past you moron");
        }

        Optional<T> optionalLatest = this.findLatestFor(anime, scheduleAt.toOffsetDateTime());
        long left = anime.getTotal() - optionalLatest.map(AnimeNightMeta::getLastEpisode)
                                                     .orElseGet(anime::getWatched);

        while (left > 0) {
            long nextAmount = Math.min(amount, left);

            Optional<T> optionallyScheduled = this.scheduleAt(anime, nextAmount, scheduleAt, scheduler);

            if (optionallyScheduled.isPresent()) {
                left -= nextAmount;
                continue;
            }

            scheduleAt = incrementalTime.apply(scheduleAt);

            if (scheduleAt.isAfter(securityLimit)) {
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

    public boolean delay(long value, TimeUnit unit, Predicate<OffsetDateTime> predicate, Consumer<T> onUpdate) {

        // Retrieve delayable entities.
        List<T> delayable = this.nights.stream()
                                       .filter(event -> predicate.test(event.getStartDateTime()))
                                       .sorted(Comparator.comparing(AnimeNightMeta::getStartDateTime).reversed())
                                       .toList();

        // Dry run - We first check if we can delay safely.
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
                return false;
            }
        }

        // Hey, we can delay the schedule !
        for (T event : delayable) {
            // Apply delay
            event.setStartDateTime(event.getStartDateTime().plus(value, unit.toChronoUnit()));
            onUpdate.accept(event);
        }
        // Delay success !
        return true;
    }
}
