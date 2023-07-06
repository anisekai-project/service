package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.events.animenight.AnimeNightCreatedEvent;
import me.anisekai.toshiko.events.animenight.AnimeNightFinishedEvent;
import me.anisekai.toshiko.events.animenight.AnimeNightStartedEvent;
import me.anisekai.toshiko.events.animenight.AnimeNightUpdatedEvent;
import me.anisekai.toshiko.exceptions.nights.AnimeNightOverlappingException;
import me.anisekai.toshiko.helpers.AnimeNightScheduler;
import me.anisekai.toshiko.repositories.AnimeNightRepository;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class AnimeNightService {

    public static final Supplier<OffsetDateTime> DELAY_TIME_LIMIT = () -> ZonedDateTime.now()
                                                                                       .plusHours(6)
                                                                                       .toOffsetDateTime();

    private static final Logger                    LOGGER = LoggerFactory.getLogger(AnimeNightService.class);
    private final        AnimeNightRepository      repository;
    private final        ApplicationEventPublisher publisher;

    public AnimeNightService(AnimeNightRepository repository, ApplicationEventPublisher publisher) {

        this.repository = repository;
        this.publisher  = publisher;
    }

    public AnimeNightRepository getRepository() {

        return this.repository;
    }

    public void refreshAll() {

        List<AnimeNight> nights = this.repository.findAllByStatusIn(AnimeNight.WATCHABLE);

        nights.stream()
              .map(night -> new AnimeNightUpdatedEvent(this, night))
              .forEach(this.publisher::publishEvent);
    }

    /**
     * Create a new {@link AnimeNightScheduler} with initial data fetched from the database.
     *
     * @return A new instance of {@link AnimeNightScheduler}.
     */
    public AnimeNightScheduler<AnimeNight> createScheduler() {

        return new AnimeNightScheduler<>(this.repository.findAllByStatusIn(AnimeNight.WATCHABLE));
    }

    /**
     * Try to schedule an {@link AnimeNight}.
     *
     * @param anime
     *         The {@link Anime} that should be scheduled
     * @param time
     *         When the {@link Anime} should be scheduled
     * @param amount
     *         How many episode of the {@link Anime} should be scheduled
     *
     * @return The newly scheduled {@link AnimeNight}.
     *
     * @throws AnimeNightOverlappingException
     *         Thrown when the {@link Anime} couldn't be scheduled because it would overlap an already existing
     *         {@link AnimeNight}.
     */
    public AnimeNight schedule(Anime anime, ZonedDateTime time, long amount) {

        LOGGER.info(
                "Trying scheduling anime {} for {} episodes at {}:{}...",
                anime.getId(),
                amount,
                time.getHour(),
                time.getMinute()
        );

        AnimeNight night = this.createScheduler()
                               .scheduleAt(
                                       anime,
                                       amount,
                                       time,
                                       booking -> this.repository.save(new AnimeNight(booking))
                               );

        LOGGER.info("The anime has been scheduled as AnimeNight {}", night.getId());

        LOGGER.debug("Sending AnimeNightCreatedEvent...");
        AnimeNightCreatedEvent event = new AnimeNightCreatedEvent(this, night);
        this.publisher.publishEvent(event);

        return night;
    }

    /**
     * Try to schedule an {@link Anime} into multiple {@link AnimeNight} until there is no episode left to schedule.
     *
     * @param anime
     *         The {@link Anime} that should be scheduled
     * @param scheduleAt
     *         When the scheduling should start
     * @param amount
     *         How many episode of the {@link Anime} should be scheduled per {@link AnimeNight}
     * @param timeIncrement
     *         {@link Function} accepting the latest tried {@link ZonedDateTime} for scheduling and returning the next
     *         {@link ZonedDateTime} to try.
     *
     * @return All scheduled {@link AnimeNight}
     */
    public List<AnimeNight> scheduleAll(Anime anime, ZonedDateTime scheduleAt, long amount, Function<ZonedDateTime, ZonedDateTime> timeIncrement) {

        LOGGER.info(
                "Trying to schedule anime {} for {} episodes until there is no episodes left...",
                anime.getId(),
                amount
        );


        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        // Normalize date
        ZonedDateTime scheduleFrom = scheduleAt.withSecond(0).withNano(0);

        List<AnimeNight> nights = new ArrayList<>();
        scheduler.scheduleAllStartingAt(anime, amount, scheduleFrom, booking -> {
            AnimeNight night = this.repository.save(new AnimeNight(booking));
            nights.add(night);
            return night;
        }, timeIncrement);

        nights.forEach(night -> LOGGER.debug(
                " > {} created - {} episodes ({} -> {}) | {} -> {}",
                night.getId(),
                night.getAmount(),
                night.getFirstEpisode(),
                night.getLastEpisode(),
                night.getStartDateTime(),
                night.getEndDateTime()
        ));

        LOGGER.info("{} AnimeNights has been created.", nights.size());

        LOGGER.debug("Sending {} AnimeNightCreatedEvent...", nights.size());
        nights.stream().map(night -> new AnimeNightCreatedEvent(this, night))
              .forEach(this.publisher::publishEvent);

        return nights;
    }

    /**
     * Try to delay the whole schedule by the amount of minutes provided.
     *
     * @param minutes
     *         By how much the schedule should be delayed (in minutes)
     *
     * @return All updated {@link AnimeNight}
     *
     * @throws AnimeNightOverlappingException
     *         Thrown if an event couldn't be delayed. When this exception is thrown, no {@link AnimeNight} will be
     *         updated, even if some were not overlapping (transaction-like).
     */
    public List<AnimeNight> delay(long minutes) {

        LOGGER.info("Trying to delay AnimeNights by {} minutes...", minutes);

        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        List<AnimeNight> nights = new ArrayList<>();
        OffsetDateTime   limit  = DELAY_TIME_LIMIT.get();
        scheduler.delay(
                minutes,
                TimeUnit.MINUTES,
                time -> time.isBefore(limit),
                night -> nights.add(this.repository.save(night))
        );

        nights.forEach(night -> LOGGER.debug(
                " > {} updated - {} episodes ({} -> {}) | {} -> {}",
                night.getId(),
                night.getAmount(),
                night.getFirstEpisode(),
                night.getLastEpisode(),
                night.getStartDateTime(),
                night.getEndDateTime()
        ));

        LOGGER.info("Delayed {} AnimeNights", nights.size());

        LOGGER.debug("Sending {} AnimeNightUpdatedEvent...", nights.size());
        nights.stream().map(night -> new AnimeNightUpdatedEvent(this, night))
              .forEach(this.publisher::publishEvent);

        return nights;
    }

    /**
     * Loop through all scheduled {@link AnimeNight} from the provided {@link Anime} and check if the
     * {@link AnimeNight#getFirstEpisode()} and {@link AnimeNight#getLastEpisode()} are valid.
     * <p>
     * This is useful when an {@link AnimeNight} has been cancelled or created causing missing/duplicated scheduled
     * episodes.
     * <p>
     * This will update {@link AnimeNight} entities only when necessary to avoid unnecessary database calls.
     *
     * @param anime
     *         The {@link Anime} for which all {@link AnimeNight} will be calibrated.
     *
     * @return All updated {@link AnimeNight}
     */
    public List<AnimeNight> calibrate(Anime anime) {

        LOGGER.info("Calibrating schedule for Anime {}", anime.getId());

        AnimeNightScheduler<AnimeNight> scheduler = this.createScheduler();

        List<AnimeNight> updated = new ArrayList<>();
        scheduler.calibrate(anime, updated::add);

        updated.forEach(night -> LOGGER.debug(
                " > {} updated - {} episodes ({} -> {}) | {} -> {}",
                night.getId(),
                night.getAmount(),
                night.getFirstEpisode(),
                night.getLastEpisode(),
                night.getStartDateTime(),
                night.getEndDateTime()
        ));

        LOGGER.info("Updated {} AnimeNights", updated.size());

        LOGGER.debug("Sending {} AnimeNighUpdatedEvent...", updated.size());
        this.repository.saveAll(updated).stream()
                       .map(night -> new AnimeNightUpdatedEvent(this, night))
                       .forEach(this.publisher::publishEvent);

        return updated;
    }

    /**
     * Cancel the {@link AnimeNight} associated to the provided {@link ScheduledEvent}. Cancelling an {@link AnimeNight}
     * will remove the event without triggering {@link AnimeNightFinishedEvent} events.
     * <p>
     * Upon deletion, {@link #calibrate(Anime)} will be called using the {@link AnimeNight#getAnime()} value.
     * <p>
     * If no {@link AnimeNight} is found matching the provided {@link ScheduledEvent}, nothing will happen.
     *
     * @param event
     *         Discord {@link ScheduledEvent}
     */
    public void cancelEvent(ScheduledEvent event) {

        this.repository.findByEventId(event.getIdLong()).ifPresent(night -> {
            LOGGER.info("Cancelling AnimeNight {}...", night.getId());
            this.repository.delete(night);
            // Be sure that the delete call above has been committed
            this.repository.flush();
            this.calibrate(night.getAnime());
        });
    }

    /**
     * Close the {@link AnimeNight} associated to the provided {@link ScheduledEvent}. Closing an {@link AnimeNight}
     * will trigger the {@link AnimeNightFinishedEvent} event.
     * <p>
     * If no {@link AnimeNight} is found matching the provided {@link ScheduledEvent}, nothing will happen.
     *
     * @param event
     *         Discord {@link ScheduledEvent}
     */
    public void closeEvent(ScheduledEvent event) {

        this.repository.findByEventId(event.getIdLong()).ifPresent(night -> {
            LOGGER.info("Closing AnimeNight {}...", night.getId());
            night.setStatus(event.getStatus());
            this.repository.save(night);

            LOGGER.debug("Sending AnimeNightFinishedEvent...");
            this.publisher.publishEvent(new AnimeNightFinishedEvent(this, night, event));
        });
    }

    /**
     * Open the {@link AnimeNight} associated to the provided {@link ScheduledEvent}. Opening an {@link AnimeNight} will
     * trigger the {@link AnimeNightStartedEvent} event.
     * <p>
     * If no {@link AnimeNight} is found matching the provided {@link ScheduledEvent}, nothing will happen.
     *
     * @param event
     *         Discord {@link ScheduledEvent}
     */
    public void openEvent(ScheduledEvent event) {

        this.repository.findByEventId(event.getIdLong()).ifPresent(night -> {
            LOGGER.info("Opening AnimeNight {}...", night.getId());
            night.setStatus(event.getStatus());
            this.repository.save(night);

            LOGGER.debug("Sending AnimeNightStartedEvent...");
            this.publisher.publishEvent(new AnimeNightStartedEvent(this, night, event));
        });
    }

}
