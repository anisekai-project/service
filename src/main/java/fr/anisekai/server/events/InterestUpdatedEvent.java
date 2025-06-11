package fr.anisekai.server.events;

import fr.anisekai.server.entities.Interest;

public class InterestUpdatedEvent<V> extends EntityUpdatedEventAdapter<Interest, V> {

    public InterestUpdatedEvent(Object source, Interest entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
