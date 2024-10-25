package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.api.plannifier.EventScheduler;
import me.anisekai.api.plannifier.data.BookedSpot;
import me.anisekai.api.plannifier.exceptions.GroupedScheduleException;
import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.SchedulerManager;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.modules.shizue.repositories.BroadcastRepository;
import me.anisekai.modules.shizue.services.RateLimitedTaskService;
import me.anisekai.modules.shizue.services.proxy.BroadcastProxyService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.tasks.RemoveBroadcastTask;
import me.anisekai.modules.toshiko.tasks.UpdateBroadcastTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class BroadcastDataService extends DataService<Broadcast, Long, IBroadcast, BroadcastRepository, BroadcastProxyService> implements SchedulerManager<Anime, IBroadcast, Broadcast> {

    private static final List<ScheduledEvent.Status> WATCHABLE = Arrays.asList(
            ScheduledEvent.Status.ACTIVE,
            ScheduledEvent.Status.SCHEDULED,
            ScheduledEvent.Status.UNKNOWN
    );

    private final ApplicationEventPublisher publisher;
    private final RateLimitedTaskService    task;
    private final JdaStore                  store;

    public BroadcastDataService(BroadcastProxyService proxy, ApplicationEventPublisher publisher, RateLimitedTaskService task, JdaStore store) {

        super(proxy);
        this.publisher = publisher;
        this.task      = task;
        this.store     = store;
    }

    public EventScheduler<Anime, IBroadcast, Broadcast> createScheduler() {

        return new EventScheduler<>(
                this,
                this.fetchAll(r -> r.findAllByStatusInAndScheduledIsTrue(WATCHABLE))
        );
    }

    @Override
    public Broadcast create(Plannifiable<Anime> plannifiable) {

        return this.getProxy().create(broadcast -> {
            broadcast.setWatchTarget(plannifiable.getWatchTarget());
            broadcast.setStartingAt(plannifiable.getStartingAt());
            broadcast.setEpisodeCount(plannifiable.getEpisodeCount());
            broadcast.setSkipEnabled(plannifiable.isSkipEnabled());
            broadcast.setFirstEpisode(plannifiable.getFirstEpisode());
            broadcast.setScheduled(true);
        });
    }

    @Override
    public Broadcast update(Broadcast entity, Consumer<IBroadcast> updateHook) {

        return this.getProxy().modify(entity, updateHook);
    }

    @Override
    public List<Broadcast> update(List<Broadcast> entities, Consumer<IBroadcast> updateHook) {

        return entities.stream()
                       .map(broadcast -> this.update(broadcast, updateHook))
                       .toList();
    }

    @Override
    public boolean delete(Broadcast entity) {

        Guild guild = this.store.getBotGuild();

        // Tag the entity
        Broadcast updated = this.getProxy().modify(entity, broadcast -> {
            broadcast.setScheduled(false);
            broadcast.setProgress(false);
        });
        this.task.queue(new RemoveBroadcastTask(guild, updated));
        return true;
    }

    /**
     * Schedule one {@link Broadcast} with the provided information.
     *
     * @param anime
     *         The {@link Anime} that will be watched.
     * @param time
     *         The {@link ZonedDateTime} at which the {@link Broadcast} should happen.
     * @param amount
     *         The amount of episode that will be watched during the {@link Broadcast}.
     *
     * @return The scheduled {@link Broadcast}.
     */
    public Broadcast schedule(Anime anime, ZonedDateTime time, long amount) {

        return this.createScheduler().schedule(new BookedSpot<>(anime, time, amount));
    }

    /**
     * Schedule multiple {@link Broadcast} with the provided information, until no episode can be scheduled anymore.
     *
     * @param anime
     *         The {@link Anime} that will be watched.
     * @param time
     *         The {@link ZonedDateTime} at which the first {@link Broadcast} should happen.
     * @param amount
     *         The amount of episode that will be watched during each {@link Broadcast}.
     * @param timeSlider
     *         {@link Function} that will modify the time for the next {@link Broadcast} once one has been scheduled.
     *         Usually, methods like {@link ZonedDateTime#plus(TemporalAmount)} are used here.
     *
     * @return All scheduled {@link Broadcast}.
     */
    public List<Broadcast> schedule(Anime anime, ZonedDateTime time, long amount, Function<ZonedDateTime, ZonedDateTime> timeSlider) {

        if (anime.getEpisodeCount() <= 0) { // We can't reliably schedule without knowing the max value.
            return Collections.emptyList();
        }

        EventScheduler<Anime, IBroadcast, Broadcast> scheduler     = this.createScheduler();
        ZonedDateTime                                startingAt    = time;
        List<BookedSpot<Anime>>                      spots         = new ArrayList<>();
        long                                         scheduleCount = anime.getEpisodeCount() - anime.getEpisodeWatched();

        while (scheduleCount > 0) {

            long              spotAmount = scheduleCount - amount < 0 ? scheduleCount : amount;
            BookedSpot<Anime> spot       = new BookedSpot<>(anime, startingAt, spotAmount);

            if (!scheduler.canSchedule(spot)) {
                throw new GroupedScheduleException("Encountered conflict at " + startingAt.toString());
            }

            spots.add(spot);
            startingAt = timeSlider.apply(startingAt);
            scheduleCount -= spotAmount;
        }

        return spots.stream()
                    .map(scheduler::schedule)
                    .toList();
    }

    /**
     * Queue {@link UpdateBroadcastTask} for each upcoming {@link Broadcast}, allowing to synchronize events on
     * Discord.
     *
     * @return The number of generated {@link UpdateBroadcastTask}.
     */
    public int refresh() {

        Guild guild = this.store.getBotGuild();

        List<Broadcast> broadcasts = this.fetchAll(
                repository -> repository.findAllByStatus(ScheduledEvent.Status.SCHEDULED)
        );

        broadcasts.stream()
                  .map(broadcast -> new UpdateBroadcastTask(guild, broadcast))
                  .forEach(this.task::queue);

        return broadcasts.size();
    }

    private Broadcast setBroadcastStatus(ScheduledEvent event, ScheduledEvent.Status status, boolean scheduled) {

        return this.getProxy().modify(
                repository -> repository.findByEventId(event.getIdLong()),
                broadcast -> {
                    broadcast.setStatus(status);
                    broadcast.setScheduled(scheduled);
                }
        );
    }

    /**
     * Mark the {@link Broadcast} associated to the provided {@link ScheduledEvent} as canceled.
     *
     * @param event
     *         The discord {@link ScheduledEvent}
     *
     * @return The updated {@link IBroadcast}
     */
    public Broadcast cancel(ScheduledEvent event) {

        return this.setBroadcastStatus(event, ScheduledEvent.Status.CANCELED, false);
    }

    /**
     * Mark the {@link Broadcast} associated to the provided {@link ScheduledEvent} as completed.
     *
     * @param event
     *         The discord {@link ScheduledEvent}
     *
     * @return The updated {@link IBroadcast}
     */
    public IBroadcast close(ScheduledEvent event) {

        return this.setBroadcastStatus(event, ScheduledEvent.Status.COMPLETED, false);
    }

    /**
     * Mark the {@link Broadcast} associated to the provided {@link ScheduledEvent} as ongoing.
     *
     * @param event
     *         The discord {@link ScheduledEvent}
     *
     * @return The updated {@link IBroadcast}
     */
    public IBroadcast open(ScheduledEvent event) {

        return this.setBroadcastStatus(event, ScheduledEvent.Status.ACTIVE, true);
    }

}
