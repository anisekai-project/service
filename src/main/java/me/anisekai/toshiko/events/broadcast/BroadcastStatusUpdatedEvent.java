package me.anisekai.toshiko.events.broadcast;

import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.EntityUpdatedEvent;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class BroadcastStatusUpdatedEvent extends EntityUpdatedEvent<Broadcast, ScheduledEvent.Status> {

    public BroadcastStatusUpdatedEvent(Object source, Broadcast entity, ScheduledEvent.Status previous, ScheduledEvent.Status current) {

        super(source, entity, previous, current);
    }

}
