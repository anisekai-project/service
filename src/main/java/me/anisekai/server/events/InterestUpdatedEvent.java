package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Interest;

public class InterestUpdatedEvent<V> extends EntityUpdatedEvent<Interest, V> {

    public InterestUpdatedEvent(Object source, Interest entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
