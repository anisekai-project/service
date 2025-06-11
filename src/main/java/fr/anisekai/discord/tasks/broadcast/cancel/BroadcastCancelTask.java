package fr.anisekai.discord.tasks.broadcast.cancel;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.discord.JDAStore;
import fr.anisekai.discord.tasks.broadcast.BroadcastTask;
import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.services.BroadcastService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastCancelTask extends BroadcastTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(BroadcastCancelTask.class);

    public BroadcastCancelTask(JDAStore store, BroadcastService service) {

        super(store, service);
    }

    @Override
    public void execute(ITimedAction timer, AnisekaiJson params) throws Exception {

        Broadcast broadcast = this.getService().fetch(params.getLong(OPT_BROADCAST));
        Guild     guild     = this.getGuild();

        if (broadcast.getEventId() == null) {
            throw new IllegalStateException("Can't cancel broadcast with no event ID.");
        }

        long           eventId = broadcast.getEventId();
        ScheduledEvent event   = guild.getScheduledEventById(eventId);

        if (event == null) {
            throw new IllegalStateException("Could not find the associated scheduled event.");
        }

        if (event.getStatus() == ScheduledEvent.Status.ACTIVE) {
            event.getManager().setStatus(ScheduledEvent.Status.COMPLETED).complete();
        } else if (event.getStatus() == ScheduledEvent.Status.SCHEDULED) {
            event.getManager().setStatus(ScheduledEvent.Status.CANCELED).complete();
        } else {
            throw new IllegalStateException("Could not update scheduled event with status " + event.getStatus());
        }
    }

}
