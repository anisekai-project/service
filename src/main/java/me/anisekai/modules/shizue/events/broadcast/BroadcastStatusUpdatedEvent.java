package me.anisekai.modules.shizue.events.broadcast;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.Broadcast;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class BroadcastStatusUpdatedEvent extends EntityUpdatedEvent<Broadcast, ScheduledEvent.Status> {

    public BroadcastStatusUpdatedEvent(Object source, Broadcast entity, ScheduledEvent.Status previous, ScheduledEvent.Status current) {

        super(source, entity, previous, current);
    }

}
