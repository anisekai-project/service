package fr.anisekai.server.events;

import fr.anisekai.server.entities.Task;

/**
 * Event notifying when a {@link Task} is being inserted in the database.
 */
public class TaskUpdatedEvent<V> extends EntityUpdatedEventAdapter<Task, V> {

    public TaskUpdatedEvent(Object source, Task entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
