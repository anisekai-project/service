package fr.anisekai.server.events;

import fr.anisekai.server.entities.Broadcast;

public class BroadcastUpdatedEvent<V> extends EntityUpdatedEventAdapter<Broadcast, V> {

    public BroadcastUpdatedEvent(Object source, Broadcast entity, V oldValue, V newValue) {

        super(source, entity, oldValue, newValue);
    }

}
