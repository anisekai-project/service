package me.anisekai.discord.tasks.watchlist.update;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.WatchlistService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class WatchlistUpdateFactory implements TaskFactory<WatchlistUpdateTask> {

    public static final String NAME = "watchlist:update";

    private final TaskService      service;
    private final AnimeService     animeService;
    private final InterestService  interestService;
    private final WatchlistService watchlistService;
    private final JDAStore         store;

    public WatchlistUpdateFactory(TaskService service, AnimeService animeService, InterestService interestService, WatchlistService watchlistService, JDAStore store) {

        this.service          = service;
        this.animeService     = animeService;
        this.interestService  = interestService;
        this.watchlistService = watchlistService;
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
    public @NotNull WatchlistUpdateTask create() {

        return new WatchlistUpdateTask(
                this.animeService,
                this.interestService,
                this.watchlistService,
                this.store
        );
    }

    /**
     * Check if this {@link TaskFactory} has named tasks. Named tasks usually mean that each {@link TaskExecutor}
     * created is associated to a specific {@link IEntity} and thus will have a specific name for each of them.
     *
     * @return True if this {@link TaskFactory} handles named task, false otherwise.
     */
    @Override
    public boolean hasNamedTask() {

        return true;
    }

    public Task queue(AnimeStatus status) {

        return this.queue(status, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(AnimeStatus status, long priority) {

        String        name      = String.format("%s:%s", this.getName(), status.name().toLowerCase());
        BookshelfJson arguments = new BookshelfJson();
        arguments.put(WatchlistUpdateTask.OPTION_WATCHLIST, status.name().toLowerCase());

        return this.service.queue(this, name, arguments, priority);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
