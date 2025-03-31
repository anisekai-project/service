package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.api.plannifier.EventScheduler;
import me.anisekai.api.plannifier.data.BookedSpot;
import me.anisekai.api.plannifier.interfaces.Scheduler;
import me.anisekai.api.plannifier.interfaces.SchedulerManager;
import me.anisekai.api.plannifier.interfaces.entities.Plannifiable;
import me.anisekai.discord.tasks.broadcast.cancel.BroadcastCancelFactory;
import me.anisekai.discord.tasks.broadcast.schedule.BroadcastScheduleFactory;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.enums.BroadcastFrequency;
import me.anisekai.server.enums.BroadcastStatus;
import me.anisekai.server.interfaces.IBroadcast;
import me.anisekai.server.proxy.BroadcastProxy;
import me.anisekai.server.repositories.BroadcastRepository;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class BroadcastService extends DataService<Broadcast, Long, IBroadcast<Anime>, BroadcastRepository, BroadcastProxy> implements SchedulerManager<Anime, IBroadcast<Anime>, Broadcast> {

    private final TaskService taskService;

    public BroadcastService(BroadcastProxy proxy, TaskService taskService) {

        super(proxy);
        this.taskService = taskService;
    }


    @Override
    public Broadcast create(Plannifiable<Anime> plannifiable) {

        return this.getProxy().create(entity -> {
            entity.setWatchTarget(plannifiable.getWatchTarget());
            entity.setStatus(BroadcastStatus.UNSCHEDULED);
            entity.setFirstEpisode(plannifiable.getFirstEpisode());
            entity.setEpisodeCount(plannifiable.getEpisodeCount());
            entity.setStartingAt(plannifiable.getStartingAt());
            entity.setSkipEnabled(plannifiable.isSkipEnabled());
        });
    }

    @Override
    public Broadcast update(Broadcast entity, Consumer<IBroadcast<Anime>> updateHook) {

        return this.mod(entity.getId(), updateHook);
    }

    @Override
    public List<Broadcast> update(List<Broadcast> entities, Consumer<IBroadcast<Anime>> updateHook) {

        List<Long> ids = entities.stream().map(Broadcast::getId).toList();
        return this.batch(repository -> repository.findAllById(ids), updateHook);
    }

    @Override
    public boolean delete(Broadcast broadcast) {

        String taskName = this.taskService.getFactory(BroadcastCancelFactory.class).asTaskName(broadcast);

        if (this.taskService.has(taskName)) {
            return true; // Cancel already planned.
        }

        String scheduleTaskName = this.taskService.getFactory(BroadcastScheduleFactory.class).asTaskName(broadcast);
        this.taskService.cancel(scheduleTaskName);

        if (broadcast.getStatus().isCancelable()) {
            this.taskService.getFactory(BroadcastCancelFactory.class).queue(broadcast);
        }

        this.mod(
                broadcast.getId(), entity -> {
                    entity.setStatus(BroadcastStatus.CANCELED);
                    entity.setDoProgress(false);
                }
        );

        return true;
    }

    public Scheduler<Anime, IBroadcast<Anime>, Broadcast> createScheduler() {

        return new EventScheduler<>(this, this.fetchAll());
    }

    public List<Broadcast> schedule(Anime anime, ZonedDateTime starting, BroadcastFrequency frequency, long amount) {

        long total = Math.abs(anime.getTotal());

        if (total == 0) {
            throw new IllegalArgumentException("Unknown amount of episodes");
        }

        Scheduler<Anime, IBroadcast<Anime>, Broadcast> scheduler = this.createScheduler();

        if (frequency.hasDateModifier()) {
            Collection<BookedSpot<Anime>> spots       = new ArrayList<>();
            long                          schedulable = total - anime.getWatched();
            ZonedDateTime                 spotTime    = starting;

            while (schedulable > 0) {
                long              spotAmount = schedulable - amount < 0 ? schedulable : amount;
                BookedSpot<Anime> spot       = new BookedSpot<>(anime, spotTime, spotAmount);

                if (!scheduler.canSchedule(spot)) {
                    throw new IllegalArgumentException("Could not schedule at " + spot.getStartingAt());
                }

                spots.add(spot);
                spotTime = frequency.getDateModifier().apply(spotTime);
                schedulable -= spotAmount;
            }

            return spots.stream().map(scheduler::schedule).collect(Collectors.toList());
        }

        BookedSpot<Anime> spot = new BookedSpot<>(anime, starting, amount);

        if (!scheduler.canSchedule(spot)) {
            throw new IllegalArgumentException("Could not schedule at " + spot.getStartingAt());
        }

        return Collections.singletonList(scheduler.schedule(spot));
    }

    public int refresh() {

        List<Broadcast> broadcasts = this.fetchAll(repo -> repo.findAllByStatus(BroadcastStatus.SCHEDULED));
        for (Broadcast broadcast : broadcasts) {
            this.taskService.getFactory(BroadcastScheduleFactory.class).queue(broadcast);
        }
        return broadcasts.size();
    }

    public int cancel() {

        List<Broadcast> broadcasts = this.fetchAll(repo -> repo.findAllByStatus(BroadcastStatus.ACTIVE));
        broadcasts.forEach(this::delete);
        return broadcasts.size();
    }


    public Broadcast cancel(Broadcast broadcast) {

        BroadcastCancelFactory factory = this.taskService.getFactory(BroadcastCancelFactory.class);
        String                 name    = factory.asTaskName(broadcast);

        if (this.taskService.has(name)) {
            this.taskService.cancel(name);
        }

        return this.mod(
                broadcast.getId(), entity -> {
                    entity.setStatus(BroadcastStatus.CANCELED);
                    entity.setDoProgress(false);
                }
        );
    }

    public Optional<Broadcast> find(ScheduledEvent event) {

        return this.getProxy().fetchEntity(repository -> repository.findByEventId(event.getIdLong()));
    }

}
