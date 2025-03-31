package me.anisekai.modules.linn.tasking.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.Task;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.globals.tasking.interfaces.TaskFactory;
import me.anisekai.modules.linn.tasking.executors.AnnouncementCreateTaskExecutor;
import me.anisekai.modules.linn.tasking.AnnouncementTaskExecutor;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.toshiko.JdaStore;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementCreateTaskFactory implements TaskFactory<AnnouncementCreateTaskExecutor> {

    public static final String           NAME = "announcement:create";
    private final       TaskingService   service;
    private final       JdaStore         store;
    private final       AnimeDataService animeService;

    public AnnouncementCreateTaskFactory(TaskingService service, JdaStore store, AnimeDataService animeService) {

        this.service      = service;
        this.store        = store;
        this.animeService = animeService;
    }

    public static Task queue(TaskingService service, IAnime anime) {

        String        taskName  = asTaskName(anime);
        BookshelfJson arguments = new BookshelfJson();
        arguments.put(AnnouncementTaskExecutor.OPT_ANIME, anime.getId());

        return service.queue(NAME, taskName, arguments);
    }

    public static String asTaskName(IAnime anime) {

        return String.format("%s:%s", NAME, anime.getId());
    }

    @Override
    public Class<AnnouncementCreateTaskExecutor> getTaskClass() {

        return AnnouncementCreateTaskExecutor.class;
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
    public AnnouncementCreateTaskExecutor create() {

        return new AnnouncementCreateTaskExecutor(this.animeService, this.store);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
