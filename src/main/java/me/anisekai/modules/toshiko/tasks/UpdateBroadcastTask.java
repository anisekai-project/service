package me.anisekai.modules.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.data.Task;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.helpers.FileDownloader;
import me.anisekai.modules.shizue.utils.BroadcastUtils;
import me.anisekai.modules.toshiko.Texts;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.managers.ScheduledEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateBroadcastTask implements Task {

    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateBroadcastTask.class);

    private final Guild     guild;
    private final Broadcast broadcast;

    public UpdateBroadcastTask(Guild guild, Broadcast broadcast) {

        this.guild     = guild;
        this.broadcast = broadcast;
    }

    @Override
    public String getName() {

        return String.format("BROADCAST:%s:UPDATE", this.broadcast.getId());
    }

    public void run() {

        if (this.broadcast.getEventId() == null) {
            return;
        }

        ScheduledEvent event = this.guild.getScheduledEventById(this.broadcast.getEventId());

        if (event == null) {
            LOGGER.error(
                    "ScheduledEvent {} (for Broadcast {}) was not found !",
                    this.broadcast.getEventId(),
                    this.broadcast.getId()
            );
            return;
        }

        Anime anime = this.broadcast.getAnime();
        if (this.broadcast.getFirstEpisode() > anime.getTotal()) {
            event.getManager().setStatus(ScheduledEvent.Status.CANCELED).complete();
            return;
        }

        String name = Texts.truncate(this.broadcast.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);

        ScheduledEventManager manager = event.getManager()
                                             .setName(name)
                                             .setStartTime(this.broadcast.getStartingAt())
                                             .setEndTime(this.broadcast.getEndingAt())
                                             .setDescription(BroadcastUtils.asEpisodeDescription(this.broadcast));

        try {
            byte[] imgData = FileDownloader.downloadAnimeCard(this.broadcast.getAnime());
            manager.setImage(Icon.from(imgData)).complete();
        } catch (Exception e) {
            Sentry.captureException(e);
            manager.complete();
        }
    }

}
