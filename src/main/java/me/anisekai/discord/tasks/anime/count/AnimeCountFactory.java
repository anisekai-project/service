package me.anisekai.discord.tasks.anime.count;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class AnimeCountFactory implements TaskFactory<AnimeCountTask> {

    public static final String       NAME = "anime:count";
    private final       TaskService  service;
    private final       AnimeService animeService;
    private final       JDAStore     store;

    public AnimeCountFactory(TaskService service, AnimeService animeService, JDAStore store) {

        this.service      = service;
        this.animeService = animeService;
        this.store        = store;
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
    public @NotNull AnimeCountTask create() {

        return new AnimeCountTask(this.animeService, this.store);
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

        return this.queue(Task.PRIORITY_DEFAULT);
    }

    public Task queue(long priority) {

        return this.service.queue(this, priority);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
