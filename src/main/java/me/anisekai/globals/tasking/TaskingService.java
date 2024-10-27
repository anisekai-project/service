package me.anisekai.globals.tasking;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import io.sentry.Sentry;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.globals.tasking.interfaces.TaskFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TaskingService {

    private final Collection<TaskFactory<?>> factories = new HashSet<>();
    private final TaskRepository             repository;

    public TaskingService(TaskRepository repository) {

        this.repository = repository;
    }

    /**
     * Create a new {@link Task} and queue it.
     *
     * @param factory
     *         The {@link TaskFactory} to use to create the {@link TaskExecutor}.
     * @param name
     *         A name for the {@link Task} to create.
     */
    public Task queue(String factory, String name) {

        return this.queue(factory, name, new BookshelfJson());
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
    public Task queue(String factory, String name, BookshelfJson arguments) {

        Optional<TaskFactory<?>> optionalFactory = this.factories
                .stream()
                .filter(item -> item.getName().equals(factory))
                .findAny();

        if (optionalFactory.isEmpty()) {
            throw new IllegalStateException("Tried to register a task on a unregistered factory " + factory);
        }

        TaskFactory<?> taskFactory = optionalFactory.get();

        if (!taskFactory.allowDuplicated() && this.hasTaskScheduled(name)) {
            return null;
        }

        Task task = new Task();
        task.setFactory(factory);
        task.setName(name);
        task.setState(TaskState.SCHEDULED);
        task.setArguments(arguments);
        return this.repository.save(task);
    }

    /**
     * Register the {@link TaskFactory} into this {@link TaskingService}.
     *
     * @param factory
     *         The {@link TaskFactory} to register.
     */
    public void registerFactory(TaskFactory<?> factory) {

        this.factories.add(factory);
    }

    @Scheduled(cron = "0/5 * * * * *")
    private void execute() {

        Optional<Task> optionalTask = this.repository.getFirst(TaskState.SCHEDULED);

        if (optionalTask.isEmpty()) return;
        Task task = optionalTask.get();

        Optional<TaskFactory<?>> optionalFactory = this.factories
                .stream()
                .filter(factory -> factory.getName().equals(task.getFactory()))
                .findAny();

        if (optionalFactory.isEmpty()) {
            task.setState(TaskState.FAILED);
            this.repository.save(task);
            return;
        }

        try (ITimedAction timer = ITimedAction.create()) {
            timer.open("task", task.getFactory(), "Execution of the task");

            timer.action("Pre-Execution Hook", "Update basic task data");
            TaskFactory<?> factory  = optionalFactory.get();
            TaskExecutor   executor = factory.create();

            if (executor.validateParams(task.getArguments())) {
                task.setState(TaskState.EXECUTING);
                task.setStartedAt(ZonedDateTime.now());
                task.setCompletedAt(null);
                this.repository.save(task);
            } else {
                task.setState(TaskState.FAILED);
                task.setStartedAt(null);
                task.setCompletedAt(null);
                this.repository.save(task);
                return; // Early exit
            }
            timer.endAction();

            timer.action("Execute Task", "Run the queued task");
            try {

                executor.execute(timer, task.getArguments());

                timer.action("Handle Success", "Handle task execution success");
                task.setCompletedAt(ZonedDateTime.now());
                task.setState(TaskState.SUCCEEDED);
                this.repository.save(task);
                timer.endAction();

            } catch (Exception e) {

                timer.action("Handle Failure", "Handle task execution failure");
                task.setStartedAt(null);
                task.setState(TaskState.SCHEDULED);
                task.setFailureCount(task.getFailureCount() + 1);

                if (task.getFailureCount() == 3) {
                    task.setState(TaskState.FAILED);
                }
                this.repository.save(task);
                timer.endAction();

                Sentry.withScope(scope -> {
                    Map<String, Object> context = new HashMap<>();
                    context.put("id", task.getId());
                    context.put("factory", task.getFactory());
                    context.put("name", task.getName());
                    context.put("params", task.getArguments().toString());

                    scope.setContexts("Task", context);
                    Sentry.captureException(e);
                });
            }
            timer.endAction();
        }
    }

    public boolean hasTaskScheduled(String name) {

        return this.repository.existsByNameAndStateIn(name, List.of(TaskState.SCHEDULED));
    }

}
