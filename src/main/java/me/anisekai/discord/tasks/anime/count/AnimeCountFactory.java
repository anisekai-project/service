package me.anisekai.discord.tasks.anime.count;

import jakarta.annotation.PostConstruct;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.TaskService;
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

    @Override
    public @NotNull String getName() {

        return NAME;
    }

    @Override
    public @NotNull AnimeCountTask create() {

        return new AnimeCountTask(this.animeService, this.store);
    }

    @Override
    public boolean hasNamedTask() {

        return false;
    }

    public Task queue() {

        return this.queue(Task.PRIORITY_DEFAULT);
    }

    public Task queue(byte priority) {

        return this.service.queue(this, priority);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}
