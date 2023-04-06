package me.anisekai.toshiko.helpers;

import me.anisekai.toshiko.data.BookedAnimeNight;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class AnimeNightScheduler<T extends AnimeNightMeta> {

    private static final Logger                      LOGGER   = LoggerFactory.getLogger(AnimeNightScheduler.class);
    public final static  List<ScheduledEvent.Status> STATUSES = Arrays.asList(ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED);

    private final Set<T> nights;

    public AnimeNightScheduler(Collection<T> sourceData) {

        this.nights = new HashSet<>(sourceData);
    }

    public Optional<T> findLatestFor(Anime anime, OffsetDateTime before) {

        return this.nights.stream()
                          .filter(night -> night.getAnime().equals(anime))
                          .filter(night -> night.getStartDateTime().isBefore(before))
                          .max(Comparator.comparing(AnimeNightMeta::getStartDateTime));
    }

    public Optional<T> findFirstFor(Anime anime, OffsetDateTime after) {

        return this.nights.stream()
                          .filter(night -> night.getAnime().equals(anime))
                          .filter(night -> night.getStartDateTime().isAfter(after))
                          .min(Comparator.comparing(AnimeNightMeta::getStartDateTime));
    }

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

    public Optional<T> scheduleNow(Anime anime, long amount, OffsetTime at, Function<BookedAnimeNight, T> scheduler) {

        ZonedDateTime scheduleTime = ZonedDateTime.now()
                                                  .withHour(at.getHour())
                                                  .withMinute(at.getMinute())
                                                  .withSecond(0);

        if (scheduleTime.isBefore(ZonedDateTime.now())) {
            scheduleTime = scheduleTime.plusDays(1);
        }

        return this.scheduleAt(anime, amount, scheduleTime, scheduler);
    }

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

    public void scheduleAll(Anime anime, long amount, OffsetTime at, Function<BookedAnimeNight, T> scheduler) {

        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(at.getHour()).withMinute(at.getMinute()).withSecond(0).withNano(0);

        if (scheduleAt.isBefore(now)) {
            scheduleAt = scheduleAt.plusDays(1);
        }

        this.scheduleAllStartingAt(anime, amount, scheduleAt, scheduler, zdt -> zdt.plusDays(1));
    }

    public void scheduleAllWeekly(Anime anime, long amount, OffsetTime at, DayOfWeek day, Function<BookedAnimeNight, T> scheduler) {

        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(at.getHour()).withMinute(at.getMinute()).withSecond(0).withNano(0);

        // Why there is no `setDayOfWeek` ? smh
        while (scheduleAt.getDayOfWeek() != day) {
            scheduleAt = scheduleAt.plusDays(1);
        }

        // This could happen only if the loop above did not execute once.
        if (scheduleAt.isBefore(now)) {
            scheduleAt = scheduleAt.plusDays(7);
        }

        this.scheduleAllStartingAt(anime, amount, scheduleAt, scheduler, zdt -> zdt.plusDays(7));
    }

    public void calibrate(Anime anime, Consumer<T> onUpdate) {

        this.calibrate(Collections.singleton(anime), onUpdate);
    }

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
}
