package me.anisekai.modules.shizue.events.broadcast;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.Broadcast;

public class BroadcastDurationUpdatedEvent extends EntityUpdatedEvent<Broadcast, Long> {

    public BroadcastDurationUpdatedEvent(Object source, Broadcast entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
