package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Task;
import fr.anisekai.server.entities.adapters.TaskEventAdapter;
import fr.anisekai.server.events.TaskCreatedEvent;
import fr.anisekai.server.exceptions.task.TaskNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TaskProxy extends ProxyService<Task, Long, TaskEventAdapter, TaskRepository> {

    public TaskProxy(ApplicationEventPublisher publisher, TaskRepository repository) {

        super(publisher, repository, Task::new);
    }

    /**
     * Same as {@link #fetchEntity(Function)} but should ensure that the selector should not return any empty optional
     * instance by throwing any {@link RuntimeException} using {@link Optional#orElseThrow(Supplier)}.
     *
     * @param selector
     *         The selector to use to retrieve the entity.
     *
     * @return The entity instance.
     */
    @Override
    public Task getEntity(Function<TaskRepository, Optional<Task>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(TaskNotFoundException::new);
    }

    public Task create(Consumer<TaskEventAdapter> consumer) {

        return this.create(consumer, TaskCreatedEvent::new);
    }

}
