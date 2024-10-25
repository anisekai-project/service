package me.anisekai.modules.shizue.events.broadcast;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.shizue.entities.Broadcast;

public class BroadcastCreatedEvent extends EntityCreatedEvent<Broadcast> {

    public BroadcastCreatedEvent(Object source, Broadcast entity) {

        super(source, entity);
    }

}
