package me.anisekai.toshiko.events.broadcast;

import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class BroadcastImageUrlUpdatedEvent extends EntityUpdatedEvent<Broadcast, String> {

    public BroadcastImageUrlUpdatedEvent(Object source, Broadcast entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
