package me.anisekai.modules.linn.tasking.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.globals.tasking.Task;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.globals.tasking.interfaces.TaskFactory;
import me.anisekai.modules.linn.tasking.executors.AnimeCountTaskExecutor;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.toshiko.JdaStore;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Component;

@Component
public class AnimeCountTaskFactory implements TaskFactory<AnimeCountTaskExecutor> {

    public static final String           NAME = "anime-count";
    private final       TaskingService   service;
    private final       AnimeDataService animeService;
    private final       JdaStore         store;

    public AnimeCountTaskFactory(TaskingService service, AnimeDataService animeService, JdaStore store) {

        this.service      = service;
        this.animeService = animeService;
        this.store        = store;
    }

    public static Task queue(TaskingService service) {

        return service.queue(NAME, asTaskName());
    }

    public static String asTaskName() {

        return NAME;
    }

    @Override
    public Class<AnimeCountTaskExecutor> getTaskClass() {

        return AnimeCountTaskExecutor.class;
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
    public AnimeCountTaskExecutor create() {

        TextChannel channel = this.store.getWatchlistChannel();
        return new AnimeCountTaskExecutor(this.animeService, channel);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
