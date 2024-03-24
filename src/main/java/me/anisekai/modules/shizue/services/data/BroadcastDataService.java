package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.helpers.BroadcastScheduler;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.modules.shizue.repositories.BroadcastRepository;
import me.anisekai.modules.shizue.services.RateLimitedTaskService;
import me.anisekai.modules.shizue.services.proxy.BroadcastProxyService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.tasks.UpdateBroadcastTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class BroadcastDataService extends DataService<Broadcast, Long, IBroadcast, BroadcastRepository, BroadcastProxyService> {

    private static final List<ScheduledEvent.Status> WATCHABLE = Arrays.asList(
            ScheduledEvent.Status.ACTIVE,
            ScheduledEvent.Status.SCHEDULED,
            ScheduledEvent.Status.UNKNOWN
    );

    private final RateLimitedTaskService rateLimitedTaskService;
    private final JdaStore               jdaStore;

    public BroadcastDataService(BroadcastProxyService proxy, RateLimitedTaskService rateLimitedTaskService, JdaStore jdaStore) {

        super(proxy);
        this.rateLimitedTaskService = rateLimitedTaskService;
        this.jdaStore               = jdaStore;
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
            .forEach(this.rateLimitedTaskService::queue);
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

    public List<IBroadcast> delay(TemporalAmount limit, TemporalAmount duration) {

        return this.getProxy().batch(
                repository -> repository.findAllByStatus(ScheduledEvent.Status.SCHEDULED),
                broadcasts -> {
                    ZonedDateTime timeLimit = ZonedDateTime.now().plus(limit);
                    return new BroadcastScheduler<>(broadcasts).delay(
                            duration,
                            time -> time.isBefore(timeLimit)
                    );
                }
        );
    }

    public List<IBroadcast> delay(long limit, long minutes) {

        return this.delay(Duration.ofMinutes(limit), Duration.ofMinutes(minutes));
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
