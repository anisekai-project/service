package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Selection;

public class SelectionUpdatedEvent<V> extends EntityUpdatedEvent<Selection, V> {

    public SelectionUpdatedEvent(Object source, Selection entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
