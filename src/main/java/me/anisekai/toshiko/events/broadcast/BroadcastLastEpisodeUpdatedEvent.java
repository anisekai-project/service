package me.anisekai.toshiko.events.broadcast;

import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class BroadcastLastEpisodeUpdatedEvent extends EntityUpdatedEvent<Broadcast, Long> {

    public BroadcastLastEpisodeUpdatedEvent(Object source, Broadcast entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
