package fr.anisekai.server.services;

import fr.anisekai.discord.tasks.broadcast.cancel.BroadcastCancelFactory;
import fr.anisekai.discord.tasks.broadcast.schedule.BroadcastScheduleFactory;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.entities.adapters.BroadcastEventAdapter;
import fr.anisekai.server.enums.BroadcastFrequency;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.planifier.BookedSpot;
import fr.anisekai.server.proxy.BroadcastProxy;
import fr.anisekai.server.repositories.BroadcastRepository;
import fr.anisekai.wireless.api.plannifier.EventScheduler;
import fr.anisekai.wireless.api.plannifier.interfaces.ScheduleSpotData;
import fr.anisekai.wireless.api.plannifier.interfaces.Scheduler;
import fr.anisekai.wireless.api.plannifier.interfaces.SchedulerManager;
import fr.anisekai.wireless.api.plannifier.interfaces.entities.Planifiable;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class BroadcastService extends DataService<Broadcast, Long, BroadcastEventAdapter, BroadcastRepository, BroadcastProxy> implements SchedulerManager<Anime, BroadcastEventAdapter, Broadcast> {

    private final static List<BroadcastStatus> ACTIVE_STATUSES = Arrays.asList(
            BroadcastStatus.SCHEDULED,
            BroadcastStatus.ACTIVE,
            BroadcastStatus.UNSCHEDULED
    );

    private final TaskService taskService;

    public BroadcastService(BroadcastProxy proxy, TaskService taskService) {

        super(proxy);
        this.taskService = taskService;
    }

    @Override
    public Broadcast create(Planifiable<Anime> planifiable) {

        return this.getProxy().create(entity -> {
            entity.setWatchTarget(planifiable.getWatchTarget());
            entity.setStatus(BroadcastStatus.UNSCHEDULED);
            entity.setFirstEpisode(planifiable.getFirstEpisode());
            entity.setEpisodeCount(planifiable.getEpisodeCount());
            entity.setStartingAt(planifiable.getStartingAt());
            entity.setSkipEnabled(planifiable.isSkipEnabled());
        });
    }

    @Override
    public Broadcast update(Broadcast entity, Consumer<BroadcastEventAdapter> updateHook) {

        return this.mod(entity.getId(), updateHook);
    }

    @Override
    public List<Broadcast> updateAll(List<Broadcast> entities, Consumer<BroadcastEventAdapter> updateHook) {

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

        if (broadcast.getStatus().isDiscordCancelable()) {
            this.taskService.getFactory(BroadcastCancelFactory.class).queue(broadcast);
        } else {
            this.mod(
                    broadcast.getId(), entity -> entity.setStatus(BroadcastStatus.CANCELED)
            );
        }

        return true;
    }

    public boolean hasPreviousScheduled(ScheduleSpotData<Anime> broadcast) {

        return this.getProxy()
                   .getRepository()
                   .countPreviousOf(
                           broadcast.getWatchTarget().getId(),
                           broadcast.getStartingAt(),
                           ACTIVE_STATUSES
                   ) > 0;
    }

    public Scheduler<Anime, BroadcastEventAdapter, Broadcast> createScheduler() {

        List<Broadcast> broadcasts = this.fetchAll(repository -> repository.findAllByStatusIn(ACTIVE_STATUSES));

        return new EventScheduler<>(this, broadcasts);
    }

    public List<Broadcast> schedule(Anime anime, ZonedDateTime starting, BroadcastFrequency frequency, int amount) {

        int total = Math.abs(anime.getTotal());

        if (total == 0) {
            throw new IllegalArgumentException("Unknown amount of episodes");
        }

        Scheduler<Anime, BroadcastEventAdapter, Broadcast> scheduler = this.createScheduler();

        if (frequency.hasDateModifier()) {
            Collection<ScheduleSpotData<Anime>> spots       = new ArrayList<>();
            int                                 schedulable = total - anime.getWatched();
            ZonedDateTime                       spotTime    = starting;

            while (schedulable > 0) {
                int               spotAmount = schedulable - amount < 0 ? schedulable : amount;
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
                broadcast.getId(), entity -> entity.setStatus(BroadcastStatus.CANCELED)
        );
    }

    public Optional<Broadcast> find(ScheduledEvent event) {

        return this.getProxy().fetchEntity(repository -> repository.findByEventId(event.getIdLong()));
    }

}
