package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Task;

/**
 * Event notifying when a {@link Task} is being inserted in the database.
 */
public class TaskUpdatedEvent<V> extends EntityUpdatedEvent<Task, V> {

    public TaskUpdatedEvent(Object source, Task entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
