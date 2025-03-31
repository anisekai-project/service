package me.anisekai.library.tasks.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.library.tasks.executors.TorrentSynchronizationTask;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TorrentSynchronizationFactory implements TaskFactory<TorrentSynchronizationTask> {

    private final static String         NAME = "torrent:sync";
    private final        TaskService    service;
    private final        TorrentService torrentService;

    public TorrentSynchronizationFactory(TaskService service, TorrentService torrentService) {

        this.service        = service;
        this.torrentService = torrentService;
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
    public @NotNull TorrentSynchronizationTask create() {

        return new TorrentSynchronizationTask(this.torrentService);
    }

    /**
     * Check if this {@link TaskFactory} has named tasks. Named tasks usually mean that each {@link TaskExecutor}
     * created is associated to a specific {@link IEntity} and thus will have a specific name for each of them.
     *
     * @return True if this {@link TaskFactory} handles named task, false otherwise.
     */
    @Override
    public boolean hasNamedTask() {

        return false;
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

    public Task queue() {

        return this.queue(Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(long priority) {

        return this.service.queue(this, priority);
    }

}
