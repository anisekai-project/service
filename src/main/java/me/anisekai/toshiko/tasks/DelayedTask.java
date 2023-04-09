package me.anisekai.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.events.AnimeNightUpdateEvent;
import me.anisekai.toshiko.tasks.entity.TaskEntry;
import net.dv8tion.jda.api.entities.ScheduledEvent;
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

    public DelayedTask() {

        this.tasks = new LinkedBlockingDeque<>();
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

    public boolean has(String name) {

        return this.tasks.stream().anyMatch(entry -> entry.getName().equalsIgnoreCase(name));
    }

    public void queue(String name, Runnable runnable) {

        this.queue(name, runnable, (e) -> {
            LOGGER.error("Runnable '{}' failure.", name, e);
        });
    }

    public void queue(String name, Runnable runnable, Consumer<Exception> failed) {

        if (this.tasks.stream().noneMatch(entry -> entry.getName().equalsIgnoreCase(name))) {
            this.tasks.offer(new TaskEntry(name, runnable, failed));
            LOGGER.info("Runnable '{}' added; There are {} items in the queue.", name, this.tasks.size());
        } else {
            LOGGER.info("Runnable '{}' not added; Already in queue.", name);
        }
    }

    @EventListener(AnimeNightUpdateEvent.class)
    public void onAnimeNightUpdate(AnimeNightUpdateEvent event) {

        if (event.getAnimeNight().getEventId() == null) {
            return;
        }

        this.queue(String.format("ANIME-NIGHT:UPDATE:%s", event.getAnimeNight().getEventId()), () -> {
            // Sanity check of reschedule
            long first = event.getAnimeNight().getFirstEpisode();
            long last  = event.getAnimeNight().getLastEpisode();
            long total = event.getAnimeNight().getAnime().getTotal();

            if (event.getAnimeNight().getEventId() == null) {
                return;
            }

            if (first > total || last > total) {
                // Yep, overflow, let's cancel this event.
                LOGGER.info("Watch overflow for scheduled event {}", event.getAnimeNight().getEventId());
                event.getGuild()
                     .retrieveScheduledEventById(event.getAnimeNight().getEventId())
                     .complete()
                     .getManager()
                     .setStatus(ScheduledEvent.Status.CANCELED)
                     .complete();
                LOGGER.info("Scheduled event cancelled !");
            } else {
                // Safe to reschedule.
                LOGGER.info("Updating scheduled event {}", event.getAnimeNight().getEventId());
                event.getGuild()
                     .retrieveScheduledEventById(event.getAnimeNight().getEventId())
                     .complete()
                     .getManager()
                     .setDescription(event.getAnimeNight().asEventDescription())
                     .setStartTime(event.getAnimeNight().getStartDateTime())
                     .setEndTime(event.getAnimeNight().getEndDateTime())
                     .complete();
                LOGGER.info("Scheduled event updated !");
            }
        });
    }

}
