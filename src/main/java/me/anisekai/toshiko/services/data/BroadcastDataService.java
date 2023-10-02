package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.helpers.BroadcastScheduler;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import me.anisekai.toshiko.interfaces.entities.IBroadcast;
import me.anisekai.toshiko.modules.discord.JdaStore;
import me.anisekai.toshiko.modules.discord.tasks.UpdateBroadcastTask;
import me.anisekai.toshiko.repositories.BroadcastRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.TaskService;
import me.anisekai.toshiko.services.proxy.BroadcastProxyService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class BroadcastDataService extends AbstractDataService<Broadcast, Long, IBroadcast, BroadcastRepository, BroadcastProxyService> {

    private static final List<ScheduledEvent.Status> WATCHABLE = Arrays.asList(
            ScheduledEvent.Status.ACTIVE,
            ScheduledEvent.Status.SCHEDULED,
            ScheduledEvent.Status.UNKNOWN
    );

    private final TaskService taskService;
    private final JdaStore    jdaStore;

    public BroadcastDataService(BroadcastProxyService proxy, TaskService taskService, JdaStore jdaStore) {

        super(proxy);
        this.taskService = taskService;
        this.jdaStore    = jdaStore;
    }

    public Broadcast create(AnimeNightMeta meta) {

        return this.getProxy().create(broadcast -> {
            broadcast.setAnime(meta.getAnime());
            broadcast.setStatus(ScheduledEvent.Status.UNKNOWN);
            broadcast.setAmount(meta.getAmount());
            broadcast.setFirstEpisode(meta.getFirstEpisode());
            broadcast.setLastEpisode(meta.getLastEpisode());
            broadcast.setStartDateTime(meta.getStartDateTime());
            broadcast.setEndDateTime(meta.getEndDateTime());
        });
    }

    public void askBroadcastRefresh() {

        Guild guild = this.jdaStore.getBotGuild();

        this.fetchAll(repository -> repository.findAllByStatus(ScheduledEvent.Status.SCHEDULED)).
            stream()
            .map(broadcast -> new UpdateBroadcastTask(guild, broadcast))
            .forEach(this.taskService::queue);
    }

    public IBroadcast schedule(Anime anime, ZonedDateTime time, long amount) {

        return this.getProxy().batch(
                repository -> repository.findAllByStatusIn(WATCHABLE),
                broadcasts -> {
                    return new BroadcastScheduler<>(broadcasts).scheduleAt(anime, amount, time, this::create);
                }
        );
    }

    public List<IBroadcast> schedule(Anime anime, ZonedDateTime time, long amount, Function<ZonedDateTime, ZonedDateTime> timeFunction) {

        return this.getProxy().batch(
                repository -> repository.findAllByStatusIn(WATCHABLE),
                broadcasts -> {
                    return new BroadcastScheduler<>(broadcasts).scheduleAllStartingAt(
                            anime,
                            amount,
                            time,
                            this::create,
                            timeFunction
                    );
                }
        );
    }

    public List<IBroadcast> delay(long limit, long minutes) {

        return this.getProxy().batch(
                repository -> repository.findAllByStatus(ScheduledEvent.Status.SCHEDULED),
                broadcasts -> {
                    ZonedDateTime timeLimit = ZonedDateTime.now().plusMinutes(limit);
                    return new BroadcastScheduler<>(broadcasts).delay(
                            minutes,
                            TimeUnit.MINUTES,
                            time -> time.isBefore(timeLimit)
                    );
                }
        );
    }

    public List<IBroadcast> calibrate(Anime anime) {

        return this.getProxy().batch(
                repository -> repository.findAllByStatusIn(WATCHABLE),
                broadcasts -> {
                    return new BroadcastScheduler<>(broadcasts).calibrate(anime);
                }
        );
    }

    public List<IBroadcast> calibrate(Iterable<Anime> animes) {

        return this.getProxy().batch(
                repository -> repository.findAllByStatusIn(WATCHABLE),
                broadcasts -> {
                    return new BroadcastScheduler<>(broadcasts).calibrate(animes);
                }
        );
    }

    public IBroadcast cancel(ScheduledEvent event) {

        return this.getProxy().modify(
                repository -> repository.findByEventId(event.getIdLong()),
                broadcast -> broadcast.setStatus(ScheduledEvent.Status.CANCELED)
        );
    }

    public IBroadcast close(ScheduledEvent event) {

        return this.getProxy().modify(
                repository -> repository.findByEventId(event.getIdLong()),
                broadcast -> broadcast.setStatus(ScheduledEvent.Status.COMPLETED)
        );
    }

    public IBroadcast open(ScheduledEvent event) {

        return this.getProxy().modify(
                repository -> repository.findByEventId(event.getIdLong()),
                broadcast -> broadcast.setStatus(ScheduledEvent.Status.ACTIVE)
        );
    }

}
