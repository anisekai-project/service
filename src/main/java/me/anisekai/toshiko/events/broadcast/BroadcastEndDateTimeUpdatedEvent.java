package me.anisekai.toshiko.events.broadcast;

import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

import java.time.ZonedDateTime;

public class BroadcastEndDateTimeUpdatedEvent extends EntityUpdatedEvent<Broadcast, ZonedDateTime> {

    public BroadcastEndDateTimeUpdatedEvent(Object source, Broadcast entity, ZonedDateTime previous, ZonedDateTime current) {

        super(source, entity, previous, current);
    }

}
