package me.anisekai.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.events.AnimeNightUpdateEvent;
import me.anisekai.toshiko.services.ToshikoService;
import me.anisekai.toshiko.tasks.entity.TaskEntry;
import me.anisekai.toshiko.utils.AnimeNights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

@Service
public class DelayedTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedTask.class);

    private final BlockingDeque<TaskEntry> tasks;
    private final ToshikoService           service;

    public DelayedTask(ToshikoService service) {

        this.service = service;
        this.tasks   = new LinkedBlockingDeque<>();
    }

    @Scheduled(cron = "0/6 * * * * *")
    public void execute() {

        TaskEntry poll = this.tasks.poll();
        if (poll != null) {
            LOGGER.info("TASK {} | Executing... ({} tasks left)", poll.getName(), this.tasks.size());
            try {
                poll.getRunnable().run();
                LOGGER.info("TASK {} | Success. ({} tasks left)", poll.getName(), this.tasks.size());
            } catch (Exception e) {
                LOGGER.warn("TASK {} | Failure. ({} tasks left)", poll.getName(), this.tasks.size());
                LOGGER.error("The task failed.", e);
                Sentry.captureException(e);
                poll.getFailed().accept(e);
            }
        }
    }

    public void queue(String name, Runnable runnable) {

        this.tasks.offer(new TaskEntry(name, runnable));
        LOGGER.info("Runnable '{}' added; There are {} items in the queue.", name, this.tasks.size());
    }

    public void queue(String name, Runnable runnable, Consumer<Exception> failed) {

        this.tasks.offer(new TaskEntry(name, runnable, failed));
        LOGGER.info("Runnable '{}' added; There are {} items in the queue.", name, this.tasks.size());
    }

    @EventListener(AnimeNightUpdateEvent.class)
    public void onAnimeNightUpdate(AnimeNightUpdateEvent event) {

        this.queue("ANIME-NIGHT:UPDATE", () -> {
            LOGGER.info("Updating scheduled event {}", event.getAnimeNight().getId());
            event.getGuild()
                 .retrieveScheduledEventById(event.getAnimeNight().getId())
                 .complete()
                 .getManager()
                 .setDescription(AnimeNights.createDescription(event))
                 .complete();
            LOGGER.info("Scheduled event updated !");
        });
    }

}
