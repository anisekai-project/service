package me.anisekai.discord.tasks.watchlist.create;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.WatchlistService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;

public class WatchlistCreateFactory implements TaskFactory<WatchlistCreateTask> {

    public static final String NAME = "watchlist:create";

    private final TaskService      service;
    private final JDAStore         store;
    private final AnimeService     animeService;
    private final InterestService  interestService;
    private final WatchlistService watchlistService;

    public WatchlistCreateFactory(TaskService service, JDAStore store, AnimeService animeService, InterestService interestService, WatchlistService watchlistService) {

        this.service          = service;
        this.store            = store;
        this.animeService     = animeService;
        this.interestService  = interestService;
        this.watchlistService = watchlistService;
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
    public @NotNull WatchlistCreateTask create() {

        return new WatchlistCreateTask(this.store, this.watchlistService, this.animeService, this.interestService);
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

    public Task queue() {

        return this.queue(Task.PRIORITY_AUTOMATIC_HIGH);
    }

    public Task queue(long priority) {

        return this.service.queue(this, priority);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
