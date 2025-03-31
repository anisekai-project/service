package me.anisekai.discord.tasks.anime.announcement;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementFactory implements TaskFactory<AnnouncementTask> {

    public static final String NAME = "announcement";

    private final TaskService     service;
    private final JDAStore        store;
    private final AnimeService    animeService;
    private final InterestService interestService;

    public AnnouncementFactory(TaskService service, JDAStore store, AnimeService animeService, InterestService interestService) {

        this.service         = service;
        this.store           = store;
        this.animeService    = animeService;
        this.interestService = interestService;
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
    public @NotNull AnnouncementTask create() {

        return new AnnouncementTask(this.animeService, this.interestService, this.store);
    }

    /**
     * Check if this {@link TaskFactory} has named task. Named task usually mean that each {@link TaskExecutor} created
     * is associated to a specific {@link IEntity}.
     *
     * @return True if this {@link TaskFactory} handles named task, false otherwise.
     */
    @Override
    public boolean hasNamedTask() {

        return true;
    }

    public Task queue(IAnime<?> anime) {

        return this.queue(anime, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(IAnime<?> anime, long priority) {

        String        name      = String.format("%s:%s", this.getName(), anime.getId());
        BookshelfJson arguments = new BookshelfJson();
        arguments.put(AnnouncementTask.OPTION_ANIME, anime.getId());

        return this.service.queue(this, name, arguments, priority);
    }

    @PostConstruct
    public void postConstruct() {

        this.service.registerFactory(this);
    }

}
