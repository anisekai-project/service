package me.anisekai.server.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.server.entities.Task;
import me.anisekai.server.events.TaskCreatedEvent;
import me.anisekai.server.exceptions.task.TaskNotFoundException;
import me.anisekai.server.interfaces.ITask;
import me.anisekai.server.repositories.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TaskProxy extends ProxyService<Task, Long, ITask, TaskRepository> {

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

    public Task create(Consumer<ITask> consumer) {

        return this.create(consumer, TaskCreatedEvent::new);
    }

}
