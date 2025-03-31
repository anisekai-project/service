package me.anisekai.modules.toshiko.tasking.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.Task;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.globals.tasking.interfaces.TaskFactory;
import me.anisekai.modules.toshiko.tasking.executors.WatchlistTaskExecutor;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.components.RankingHandler;
import me.anisekai.modules.shizue.services.data.WatchlistDataService;
import me.anisekai.modules.toshiko.JdaStore;
import org.springframework.stereotype.Component;

@Component
public class WatchlistTaskFactory implements TaskFactory<WatchlistTaskExecutor> {

    public static final String               NAME = "watchlist";
    private final       TaskingService       service;
    private final       WatchlistDataService watchlistService;
    private final       RankingHandler       ranking;
    private final       JdaStore             store;

    public WatchlistTaskFactory(TaskingService service, WatchlistDataService watchlistService, RankingHandler ranking, JdaStore store) {

        this.service          = service;
        this.watchlistService = watchlistService;
        this.ranking          = ranking;
        this.store            = store;
    }

    public static Task queue(TaskingService service, AnimeStatus status) {

        String        taskName  = asTaskName(status);
        BookshelfJson arguments = new BookshelfJson();

        arguments.put(WatchlistTaskExecutor.OPT_STATUS, status.name().toLowerCase());

        return service.queue(NAME, taskName, arguments);
    }

    public static String asTaskName(AnimeStatus status) {

        return String.format("%s:%s", NAME, status.name().toLowerCase());
    }

    @Override
    public Class<WatchlistTaskExecutor> getTaskClass() {

        return WatchlistTaskExecutor.class;
    }

    /**
     * Get this {@link TaskFactory} name, which will be used to associate it with a {@link Task}.
     *
     * @return The {@link TaskFactory} name.
     */
    @Override
    public String getName() {

        return NAME;
    }

    /**
     * Create an instance of {@link TaskExecutor}. This is useful if your task has some bean dependencies.
     *
     * @return A new {@link TaskExecutor} instance.
     */
    @Override
    public WatchlistTaskExecutor create() {

        return new WatchlistTaskExecutor(this.watchlistService, this.ranking, this.store);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
