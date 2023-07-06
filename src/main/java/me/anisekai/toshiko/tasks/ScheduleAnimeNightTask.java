package me.anisekai.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.helpers.FileDownloader;
import me.anisekai.toshiko.services.AnimeNightService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;


public class ScheduleAnimeNightTask implements Task {

    private final AnimeNightService service;
    private final Guild             guild;
    private final AnimeNight        animeNight;

    public ScheduleAnimeNightTask(AnimeNightService service, Guild guild, AnimeNight animeNight) {

        this.service    = service;
        this.guild      = guild;
        this.animeNight = animeNight;
    }

    @Override
    public String getName() {

        return String.format("ANIME-NIGHT:%s:SCHEDULE", this.animeNight.getId());
    }

    public void run() {

        if (this.animeNight.getEventId() != null) {
            return;
        }

        String name = Texts.truncate(this.animeNight.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);

        ScheduledEventAction action = this.guild.createScheduledEvent(
                name,
                "Discord",
                this.animeNight.getStartDateTime(),
                this.animeNight.getEndDateTime()
        ).setDescription(
                this.animeNight.asEventDescription()
        );

        ScheduledEvent scheduledEvent;
        try {
            byte[] imgData = FileDownloader.downloadAnimeCard(this.animeNight.getAnime());
            scheduledEvent = action.setImage(Icon.from(imgData)).complete();
        } catch (Exception e) {
            Sentry.captureException(e);
            scheduledEvent = action.complete();
        }

        this.animeNight.setEventId(scheduledEvent.getIdLong());
        this.animeNight.setStatus(scheduledEvent.getStatus());

        this.service.getRepository().save(this.animeNight);
    }

}
