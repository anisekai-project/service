package me.anisekai.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.helpers.FileDownloader;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.managers.ScheduledEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateAnimeNightTask implements Task {

    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateAnimeNightTask.class);

    private final Guild      guild;
    private final AnimeNight animeNight;

    public UpdateAnimeNightTask(Guild guild, AnimeNight animeNight) {

        this.guild      = guild;
        this.animeNight = animeNight;
    }

    @Override
    public String getName() {

        return String.format("ANIME-NIGHT:%s:UPDATE", this.animeNight.getId());
    }

    public void run() {

        if (this.animeNight.getEventId() == null) {
            return;
        }

        ScheduledEvent event = this.guild.getScheduledEventById(this.animeNight.getEventId());

        if (event == null) {
            LOGGER.error(
                    "ScheduledEvent {} (for AnimeNight {}) was not found !",
                    this.animeNight.getEventId(),
                    this.animeNight.getId()
            );
            return;
        }
        String name = Texts.truncate(this.animeNight.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);

        ScheduledEventManager manager = event.getManager()
                                             .setName(name)
                                             .setStartTime(this.animeNight.getStartDateTime())
                                             .setEndTime(this.animeNight.getEndDateTime())
                                             .setDescription(this.animeNight.asEventDescription());

        try {
            byte[] imgData = FileDownloader.downloadAnimeCard(this.animeNight.getAnime());
            manager.setImage(Icon.from(imgData)).complete();
        } catch (Exception e) {
            Sentry.captureException(e);
            manager.complete();
        }
    }

}
