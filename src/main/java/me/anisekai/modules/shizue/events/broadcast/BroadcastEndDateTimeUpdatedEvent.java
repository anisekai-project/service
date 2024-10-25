package me.anisekai.modules.shizue.events.broadcast;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.Broadcast;

import java.time.ZonedDateTime;

public class BroadcastEndDateTimeUpdatedEvent extends EntityUpdatedEvent<Broadcast, ZonedDateTime> {

    public BroadcastEndDateTimeUpdatedEvent(Object source, Broadcast entity, ZonedDateTime previous, ZonedDateTime current) {

        super(source, entity, previous, current);
    }

}
