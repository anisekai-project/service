package me.anisekai.server.events;

import me.anisekai.server.entities.Selection;

/**
 * Event notifying when a {@link Selection} is being inserted in the database.
 */
public class SelectionCreatedEvent extends EntityCreatedEventAdapter<Selection> {

    public SelectionCreatedEvent(Object source, Selection entity) {

        super(source, entity);
    }

}
