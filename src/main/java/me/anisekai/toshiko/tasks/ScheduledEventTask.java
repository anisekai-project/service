package me.anisekai.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.helpers.FileDownloader;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;


@Service
public class ScheduledEventTask {

    private final ToshikoService service;
    private final DelayedTask    task;

    public ScheduledEventTask(ToshikoService service, DelayedTask task) {

        this.service = service;
        this.task    = task;
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void run() {

        Guild            guild              = this.service.getBotGuild();
        List<AnimeNight> allByEventIdIsNull = this.service.getAnimeNightRepository().findAllByEventIdIsNullAndStatusIsNull();
        allByEventIdIsNull.sort(Comparator.comparing(AnimeNight::getStartDateTime));

        if (allByEventIdIsNull.isEmpty()) {
            FileDownloader.cleanCache();
            return;
        }

        for (AnimeNight night : allByEventIdIsNull) {
            String taskId = String.format(
                    "SCHEDULE::%s[%s->%s]",
                    night.getId(),
                    night.getFirstEpisode(),
                    night.getLastEpisode()
            );

            // Reload
            if (!this.task.has(taskId)) {
                night.setStatus(ScheduledEvent.Status.UNKNOWN);
                this.service.getAnimeNightRepository().save(night);
                this.task.queue(taskId, () -> this.schedule(guild, night));
            }
        }
    }

    private void schedule(Guild guild, AnimeNight night) {

        String         name        = Texts.truncate(night.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);
        OffsetDateTime start       = night.getStartDateTime();
        OffsetDateTime end         = night.getEndDateTime();
        String         description = night.asEventDescription();

        ScheduledEventAction action = guild.createScheduledEvent(name, "Discord", start, end)
                                           .setDescription(description);

        ScheduledEvent scheduledEvent;
        try {
            byte[] imgData = FileDownloader.downloadAnimeCard(night.getAnime());
            scheduledEvent = action.setImage(Icon.from(imgData)).complete();
        } catch (Exception e) {
            Sentry.captureException(e);
            scheduledEvent = action.complete();
        }

        night.setEventId(scheduledEvent.getIdLong());
        night.setStatus(scheduledEvent.getStatus());
        this.service.getAnimeNightRepository().save(night);
    }
}
