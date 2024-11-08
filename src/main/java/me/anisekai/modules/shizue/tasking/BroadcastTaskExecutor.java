package me.anisekai.modules.shizue.tasking;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.lib.rest.exceptions.RestException;
import fr.alexpado.lib.rest.interfaces.IRestAction;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.modules.shizue.helpers.FileDownloader;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BroadcastTaskExecutor implements TaskExecutor {

    public static final String OPT_BROADCAST = "broadcast";

    private final static Logger LOGGER = LoggerFactory.getLogger(BroadcastTaskExecutor.class);

    public @NotNull ScheduledEvent requireEvent(Guild guild, IBroadcast broadcast) {

        if (broadcast.getEventId() == null) {
            throw new IllegalStateException("Broadcast is not scheduled on Discord.");
        }

        ScheduledEvent event = guild.getScheduledEventById(broadcast.getEventId());

        if (event == null) {
            throw new IllegalStateException("Broadcast is not scheduled on Discord.");
        }
        return event;
    }

    public @Nullable Icon getBroadcastImage(ITimedAction timer, IBroadcast broadcast) throws Exception {

        timer.action("download-banner", "Downloading the event image banner");
        LOGGER.info("Downloading event image banner");
        IRestAction<byte[]> imageAction = new FileDownloader(String.format(
                "https://media.anisekai.fr/%s.png",
                broadcast.getWatchTarget().getId()
        ));
        Icon icon = null;
        try {
            icon = Icon.from(imageAction.complete());
        } catch (RestException e) {
            if (e.getCode() != 404) throw e;
        }
        timer.endAction();
        return icon;
    }


}
