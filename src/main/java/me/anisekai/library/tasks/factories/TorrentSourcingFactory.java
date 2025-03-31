package me.anisekai.library.tasks.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.library.tasks.executors.TorrentSourcingTask;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.EpisodeService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TorrentSourcingFactory implements TaskFactory<TorrentSourcingTask> {

    private static final String         NAME = "torrent:sourcing";
    private final        TaskService    service;
    private final        AnimeService   animeService;
    private final        EpisodeService episodeService;
    private final        TorrentService torrentService;

    public TorrentSourcingFactory(TaskService service, AnimeService animeService, EpisodeService episodeService, TorrentService torrentService) {

        this.service        = service;
        this.animeService   = animeService;
        this.episodeService = episodeService;
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
    public @NotNull TorrentSourcingTask create() {

        return new TorrentSourcingTask(this.animeService, this.episodeService, this.torrentService);
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

    public Task queue(String source) {

        return this.queue(source, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(String source, long priority) {

        BookshelfJson arguments = new BookshelfJson();
        arguments.put(TorrentSourcingTask.OPTION_SOURCE, source);
        return this.service.queue(this, this.getName(), arguments, priority);
    }

}
