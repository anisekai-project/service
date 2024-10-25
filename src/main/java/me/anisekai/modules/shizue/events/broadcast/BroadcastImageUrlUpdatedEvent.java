package me.anisekai.modules.shizue.events.broadcast;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.Broadcast;

public class BroadcastImageUrlUpdatedEvent extends EntityUpdatedEvent<Broadcast, String> {

    public BroadcastImageUrlUpdatedEvent(Object source, Broadcast entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
