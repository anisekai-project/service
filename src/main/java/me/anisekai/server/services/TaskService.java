package me.anisekai.server.services;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.remote.enums.TaskStatus;
import fr.anisekai.wireless.remote.interfaces.TaskEntity;
import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.entities.adapters.TaskEventAdapter;
import me.anisekai.server.persistence.DataService;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Service
public class TaskService extends DataService<Task, Long, TaskEventAdapter, TaskRepository, TaskProxy> {

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

        List<Task> tasks = this.fetchAll(repo -> repo.findAllByNameAndStatus(name, TaskStatus.SCHEDULED));
        for (Task task : tasks) {
            this.mod(task.getId(), entity -> entity.setStatus(TaskStatus.CANCELED));
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

        return this.getProxy().getRepository().findByNameAndStatusIn(name, List.of(TaskStatus.SCHEDULED));
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
    public Task queue(TaskFactory<?> factory, byte priority) {

        return this.queue(factory, factory.getName(), new AnisekaiJson(), priority);
    }

    /**
     * Create a new {@link Task} and queue it.
     *
     * @param factory
     *         The {@link TaskFactory} to use to create the {@link TaskExecutor}.
     * @param priority
     *         A priority for the {@link Task} to create.
     */
    public Task queue(Class<? extends TaskFactory<?>> factory, byte priority) {

        TaskFactory<?> factoryInstance = this.getFactory(factory);
        return this.queue(factoryInstance, factoryInstance.getName(), new AnisekaiJson(), priority);
    }

    /**
     * Create a new {@link Task} and queue it.
     *
     * @param factory
     *         The {@link TaskFactory} to use to create the {@link TaskExecutor}.
     * @param name
     *         A name for the {@link Task} to create.
     * @param arguments
     *         A {@link AnisekaiJson} to use as arguments for the {@link TaskExecutor}.
     * @param priority
     *         A priority for the {@link Task} to create.
     *
     * @return The queued {@link Task}, or {@code null} if nothing has been queued.
     */
    public Task queue(TaskFactory<?> factory, String name, AnisekaiJson arguments, byte priority) {

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
            task.setFactoryName(factory.getName());
            task.setName(name);
            task.setPriority(priority);
            task.setStatus(TaskStatus.SCHEDULED);
            task.setArguments(arguments);
        });
    }

    @Scheduled(cron = "0/5 * * * * *")
    private void execute() {

        Optional<Task> optionalTask = this.getProxy().fetchEntity(repo -> repo.getFirst(TaskStatus.SCHEDULED));

        if (optionalTask.isEmpty()) {
            return;
        }

        Task task = optionalTask.get();

        String factoryName = task.getFactoryName();
        Optional<TaskFactory<?>> optionalFactory = this.factories
                .stream()
                .filter(factory -> factory.getName().equals(factoryName))
                .findAny();

        if (optionalFactory.isEmpty()) {
            this.mod(task.getId(), entity -> entity.setStatus(TaskStatus.FAILED));
            return;
        }

        try (ITimedAction timer = ITimedAction.create()) {
            timer.open("task", task.getFactoryName(), "Execution of the task");

            timer.action("prepare", "Update basic task data");
            TaskFactory<?> factory  = optionalFactory.get();
            TaskExecutor   executor = factory.create();

            if (executor.validateParams(task.getArguments())) {
                LOGGER.debug("(Task {}) Parameters validation successful.", task.getName());

                task = this.mod(
                        task.getId(), entity -> {
                            entity.setStatus(TaskStatus.EXECUTING);
                            entity.setStartedAt(ZonedDateTime.now());
                            entity.setCompletedAt(null);
                        }
                );

            } else {
                LOGGER.warn("(Task {}) Parameters validation failed.", task.getName());

                task = this.mod(
                        task.getId(),
                        entity -> {
                            entity.setStatus(TaskStatus.FAILED);
                            entity.setStartedAt(null);
                            entity.setCompletedAt(null);
                        }
                );

                throw new IllegalArgumentException("Failed to validate arguments for task " + task.getId());
            }
            timer.endAction();

            timer.action("exec", "Run the queued task");
            try {

                LOGGER.debug("(Task {}) Executing task...", task.getName());
                executor.execute(timer, task.getArguments());
                LOGGER.debug("(Task {}) Done.", task.getId());

                timer.action("success", "Handle task execution success");

                task = this.mod(
                        task.getId(),
                        entity -> {
                            entity.setStatus(TaskStatus.SUCCEEDED);
                            entity.setCompletedAt(ZonedDateTime.now());
                        }
                );

                timer.endAction();

            } catch (Exception e) {
                timer.action("failure", "Handle task execution failure");

                LOGGER.error("(Task {}) Execution failure.", task.getName(), e);

                task = this.mod(
                        task.getId(),
                        entity -> {
                            entity.setStatus(TaskStatus.SCHEDULED);
                            entity.setStartedAt(null);

                            if (entity.failure() == MAX_TASK_FAILURE) {
                                entity.setStatus(TaskStatus.FAILED);
                            }
                        }
                );

                timer.endAction();

                Map<String, Object> context = new HashMap<>();
                context.put("id", task.getId());
                context.put("factory", task.getFactoryName());
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

    @PostConstruct
    private void controlData() {

        List<Task> tasks = this.fetchAll(repo -> repo.findAllByStatus(TaskStatus.EXECUTING));

        for (Task task : tasks) {
            LOGGER.warn("Task {} was still running when the application stopped.", task.getId());
            this.mod(task.getId(), entity -> entity.setStatus(TaskStatus.SCHEDULED));
        }
    }

}
