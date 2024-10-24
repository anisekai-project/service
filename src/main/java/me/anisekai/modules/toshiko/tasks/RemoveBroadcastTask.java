package me.anisekai.modules.toshiko.tasks;

import me.anisekai.modules.shizue.data.Task;
import me.anisekai.modules.shizue.entities.Broadcast;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveBroadcastTask implements Task {

    private final static Logger LOGGER = LoggerFactory.getLogger(RemoveBroadcastTask.class);

    private final Guild     guild;
    private final Broadcast broadcast;

    public RemoveBroadcastTask(Guild guild, Broadcast broadcast) {

        this.guild     = guild;
        this.broadcast = broadcast;
    }

    @Override
    public String getName() {

        return String.format("BROADCAST:%s:REMOVE", this.broadcast.getId());
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

        if (event.getStatus() == ScheduledEvent.Status.ACTIVE) {
            event.getManager().setStatus(ScheduledEvent.Status.COMPLETED).complete();
        } else {
            event.getManager().setStatus(ScheduledEvent.Status.CANCELED).complete();
        }
    }

}
