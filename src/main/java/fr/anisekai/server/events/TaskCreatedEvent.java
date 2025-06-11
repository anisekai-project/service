package fr.anisekai.server.events;

import fr.anisekai.server.entities.Task;

/**
 * Event notifying when a {@link Task} is being inserted in the database.
 */
public class TaskCreatedEvent extends EntityCreatedEventAdapter<Task> {

    public TaskCreatedEvent(Object source, Task entity) {

        super(source, entity);
    }

}
