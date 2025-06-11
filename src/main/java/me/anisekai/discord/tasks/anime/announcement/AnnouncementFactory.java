package me.anisekai.discord.tasks.anime.announcement;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import jakarta.annotation.PostConstruct;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.services.TaskService;
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

    @Override
    public @NotNull String getName() {

        return NAME;
    }

    @Override
    public @NotNull AnnouncementTask create() {

        return new AnnouncementTask(this.animeService, this.interestService, this.store);
    }

    @Override
    public boolean hasNamedTask() {

        return true;
    }

    public Task queue(AnimeEntity<?> anime) {

        return this.queue(anime, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(AnimeEntity<?> anime, byte priority) {

        String       name      = String.format("%s:%s", this.getName(), anime.getId());
        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(AnnouncementTask.OPTION_ANIME, anime.getId());

        return this.service.queue(this, name, arguments, priority);
    }

    @PostConstruct
    public void postConstruct() {

        this.service.registerFactory(this);
    }

}
