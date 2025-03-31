package me.anisekai.server.services;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import io.sentry.Sentry;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.enums.TaskState;
import me.anisekai.server.interfaces.ITask;
import me.anisekai.server.proxy.TaskProxy;
import me.anisekai.server.repositories.TaskRepository;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TaskService extends DataService<Task, Long, ITask, TaskRepository, TaskProxy> {

    private final static Logger LOGGER           = LoggerFactory.getLogger(TaskService.class);
    private final static int    MAX_TASK_FAILURE = 3;

    private final Collection<TaskFactory<?>> factories = new ArrayList<>();

    public TaskService(TaskProxy proxy, JDAStore store) {

        super(proxy);
    }

    /**
     * Register the {@link TaskFactory} into this {@link TaskService}.
     *
     * @param factory
     *         The {@link TaskFactory} to register.
     */
    public void registerFactory(@NotNull TaskFactory<?> factory) {

        this.factories.add(factory);
    }

    /**
     * Retrieve the {@link TaskFactory} of the provided class.
     *
     * @param factoryClass
     *         Class of the {@link TaskFactory}.
     * @param <T>
     *         Type of the {@link TaskFactory}
     *
     * @return A {@link TaskFactory} instance
     */
    public <T extends TaskFactory<?>> T getFactory(@NotNull Class<T> factoryClass) {

        return this.factories.stream()
                             .filter(factoryClass::isInstance)
                             .map(factory -> (T) factory)
                             .findFirst()
                             .orElseThrow(
                                     () -> new IllegalArgumentException("Tried to retrieve an unregistered factory " + factoryClass.getName())
                             );
    }

    /**
     * Cancel all the scheduled {@link Task} matching the provided name.
     *
     * @param name
     *         The name of the {@link Task}s to cancel.
     */
    public void cancel(String name) {

        List<Task> tasks = this.fetchAll(repo -> repo.findAllByNameAndState(name, TaskState.SCHEDULED));
        for (Task task : tasks) {
            this.mod(task.getId(), entity -> entity.setState(TaskState.CANCELED));
        }
    }

    /**
     * Find a {@link Task} matching the provided name.
     *
     * @param name
     *         The name of the {@link Task}
     *
     * @return An optional {@link Task}.
     */
    public Optional<Task> find(String name) {

        return this.getProxy().getRepository().findByNameAndStateIn(name, List.of(TaskState.SCHEDULED));
    }

    /**
     * Check if any scheduled {@link Task} match the provided name.
     *
     * @param name
     *         The name of the {@link Task}
     *
     * @return True if a {@link Task} matching the name is scheduled, false otherwise.
     */
    public boolean has(String name) {

        return this.find(name).isPresent();
    }

    /**
     * Create a new {@link Task} and queue it.
     *
     * @param factory
     *         The {@link TaskFactory} to use to create the {@link TaskExecutor}.
     * @param priority
     *         A priority for {@link Task} to create.
     */
    public Task queue(TaskFactory<?> factory, long priority) {

        return this.queue(factory, factory.getName(), new BookshelfJson(), priority);
    }

    /**
     * Create a new {@link Task} and queue it.
     *
     * @param factory
     *         The {@link TaskFactory} to use to create the {@link TaskExecutor}.
     * @param priority
     *         A priority for {@link Task} to create.
     */
    public Task queue(Class<? extends TaskFactory<?>> factory, long priority) {

        TaskFactory<?> factoryInstance = this.getFactory(factory);
        return this.queue(factoryInstance, factoryInstance.getName(), new BookshelfJson(), priority);
    }

    /**
     * Create a new {@link Task} and queue it.
     *
     * @param factory
     *         The {@link TaskFactory} to use to create the {@link TaskExecutor}.
     * @param name
     *         A name for the {@link Task} to create.
     * @param arguments
     *         A {@link BookshelfJson} to use as arguments for the {@link TaskExecutor}.
     *
     * @return The queued {@link Task}, or {@code null} if nothing has been queued.
     */
    public Task queue(TaskFactory<?> factory, String name, BookshelfJson arguments, long priority) {

        if (!this.factories.contains(factory)) { // Safeguard, just in case we forgot to call registerFactory()
            throw new IllegalStateException("Tried to register a task on a unregistered factory " + factory.getName());
        }

        // This allows any task to inherit their own priority on subtasks if necessary.
        arguments.put(TaskExecutor.OPTION_PRIORITY, priority);

        if (!factory.allowDuplicated()) {
            Optional<Task> optionalTask = this.find(name);
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();

                if (task.getPriority() >= priority) {
                    LOGGER.debug("Queuing of task '{}' dropped: The task already exists with a higher priority.", name);
                    return task;
                }

                LOGGER.info(
                        "Updating task '{}' priority from {} to {}",
                        task.getName(),
                        task.getPriority(),
                        priority
                );

                return this.mod(task.getId(), entity -> entity.setPriority(priority));
            }
        }

        LOGGER.info("Queuing task '{}' with a priority of {}.", name, priority);
        LOGGER.debug(" :: Arguments = {}", arguments);
        return this.getProxy().create(task -> {
            task.setFactory(factory.getName());
            task.setName(name);
            task.setPriority(priority);
            task.setState(TaskState.SCHEDULED);
            task.setArguments(arguments);
        });
    }

    @Scheduled(cron = "0/5 * * * * *")
    private void execute() {

        // TODO: Add a "can be executed now ?" method, so task with dependency to discord can be safely ignored until
        //  things get back on track. This would also allow the application to run without discord bot enabled.
        Optional<Task> optionalTask = this.getProxy().fetchEntity(repo -> repo.getFirst(TaskState.SCHEDULED));

        if (optionalTask.isEmpty()) return;
        Task task = optionalTask.get();

        String factoryName = task.getFactory();
        Optional<TaskFactory<?>> optionalFactory = this.factories
                .stream()
                .filter(factory -> factory.getName().equals(factoryName))
                .findAny();

        if (optionalFactory.isEmpty()) {
            this.mod(task.getId(), entity -> entity.setState(TaskState.FAILED));
            return;
        }

        try (ITimedAction timer = ITimedAction.create()) {
            timer.open("task", task.getFactory(), "Execution of the task");

            timer.action("prepare", "Update basic task data");
            TaskFactory<?> factory  = optionalFactory.get();
            TaskExecutor   executor = factory.create();

            if (executor.validateParams(task.getArguments())) {
                task = this.mod(
                        task.getId(), entity -> {
                            entity.setState(TaskState.EXECUTING);
                            entity.setStartedAt(ZonedDateTime.now());
                            entity.setCompletedAt(null);
                        }
                );
            } else {
                task = this.mod(
                        task.getId(),
                        entity -> {
                            entity.setState(TaskState.FAILED);
                            entity.setStartedAt(null);
                            entity.setCompletedAt(null);
                        }
                );
                throw new IllegalArgumentException("Failed to validate arguments for task " + task.getId());
            }
            timer.endAction();

            timer.action("exec", "Run the queued task");
            try {

                executor.execute(timer, task.getArguments());

                timer.action("success", "Handle task execution success");
                task = this.mod(
                        task.getId(),
                        entity -> {
                            entity.setState(TaskState.SUCCEEDED);
                            entity.setCompletedAt(ZonedDateTime.now());
                        }
                );
                timer.endAction();

            } catch (Exception e) {
                timer.action("failure", "Handle task execution failure");

                task = this.mod(
                        task.getId(),
                        entity -> {
                            entity.setState(TaskState.SCHEDULED);
                            entity.setStartedAt(null);
                            entity.setFailureCount(entity.getFailureCount() + 1);

                            if (entity.getFailureCount() == MAX_TASK_FAILURE) {
                                entity.setState(TaskState.FAILED);
                            }
                        }
                );
                timer.endAction();

                Map<String, Object> context = new HashMap<>();
                context.put("id", task.getId());
                context.put("factory", task.getFactory());
                context.put("name", task.getName());
                context.put("params", task.getArguments().toString());

                Sentry.withScope(scope -> {
                    scope.setContexts("Task", context);
                    Sentry.captureException(e);
                });
            }
            timer.endAction();
        }
    }

}
