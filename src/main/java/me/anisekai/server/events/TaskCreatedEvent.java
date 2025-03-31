package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Task;

/**
 * Event notifying when a {@link Task} is being inserted in the database.
 */
public class TaskCreatedEvent extends EntityCreatedEvent<Task> {

    public TaskCreatedEvent(Object source, Task entity) {

        super(source, entity);
    }

}
