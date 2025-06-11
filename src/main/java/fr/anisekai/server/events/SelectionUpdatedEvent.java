package fr.anisekai.server.events;

import fr.anisekai.server.entities.Selection;

public class SelectionUpdatedEvent<V> extends EntityUpdatedEventAdapter<Selection, V> {

    public SelectionUpdatedEvent(Object source, Selection entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
