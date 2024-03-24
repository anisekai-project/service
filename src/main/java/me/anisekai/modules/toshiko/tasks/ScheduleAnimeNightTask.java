package me.anisekai.modules.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.modules.shizue.data.Task;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.helpers.FileDownloader;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import me.anisekai.modules.shizue.utils.BroadcastUtils;
import me.anisekai.modules.toshiko.Texts;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;


public class ScheduleAnimeNightTask implements Task {

    private final BroadcastDataService service;
    private final Guild                guild;
    private final Broadcast            broadcast;

    public ScheduleAnimeNightTask(BroadcastDataService service, Guild guild, Broadcast broadcast) {

        this.service   = service;
        this.guild     = guild;
        this.broadcast = broadcast;
    }

    @Override
    public String getName() {

        return String.format("ANIME-NIGHT:%s:SCHEDULE", this.broadcast.getId());
    }

    public void run() {

        if (this.broadcast.getEventId() != null) {
            return;
        }

        Icon icon = null;

        try {
            byte[] imgData = FileDownloader.downloadAnimeCard(this.broadcast.getAnime());
            icon = Icon.from(imgData);
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        String name = Texts.truncate(this.broadcast.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);

        ScheduledEvent scheduledEvent = this.guild.createScheduledEvent(
                                                    name,
                                                    "Discord",
                                                    this.broadcast.getStartDateTime().toOffsetDateTime(),
                                                    this.broadcast.getEndDateTime().toOffsetDateTime()
                                            )
                                                  .setDescription(BroadcastUtils.asEpisodeDescription(this.broadcast))
                                                  .setImage(icon).complete();


        this.service.mod(this.broadcast.getId(), broadcast -> {
            broadcast.setEventId(scheduledEvent.getIdLong());
            broadcast.setStatus(scheduledEvent.getStatus());
        });
    }

}
