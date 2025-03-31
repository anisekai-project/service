package me.anisekai.discord.tasks.broadcast.cleaning;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;

public class BroadcastCleaningFactory implements TaskFactory<BroadcastCleaningTask> {

    public static final String           NAME = "broadcast:cleaning";
    private final       TaskService      service;
    private final       BroadcastService broadcastService;
    private final       JDAStore         store;

    public BroadcastCleaningFactory(TaskService service, BroadcastService broadcastService, JDAStore store) {

        this.service          = service;
        this.broadcastService = broadcastService;
        this.store            = store;
    }

    /**
     * Get this {@link TaskFactory} name, which will be used to associate it with a {@link Task}.
     *
     * @return The {@link TaskFactory} name.
     */
    @Override
    public @NotNull String getName() {

        return NAME;
    }

    /**
     * Create an instance of {@link TaskExecutor}. This is useful if your task has some bean dependencies.
     *
     * @return A new {@link TaskExecutor} instance.
     */
    @Override
    public @NotNull BroadcastCleaningTask create() {

        return new BroadcastCleaningTask(this.broadcastService, this.store);
    }

    /**
     * Check if this {@link TaskFactory} has named task. Named task usually mean that each {@link TaskExecutor} created
     * is associated to a specific {@link IEntity}.
     *
     * @return True if this {@link TaskFactory} handles named task, false otherwise.
     */
    @Override
    public boolean hasNamedTask() {

        return false;
    }

    public Task queue() {

        return this.queue(Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(long priority) {

        return this.service.queue(this, priority);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
