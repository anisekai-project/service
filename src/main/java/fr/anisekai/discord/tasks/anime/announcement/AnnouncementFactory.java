package fr.anisekai.discord.tasks.anime.announcement;

import fr.anisekai.discord.JDAStore;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.services.InterestService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.server.tasking.TaskBuilder;
import fr.anisekai.server.tasking.TaskFactory;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;

public abstract class AnnouncementFactory<T extends AnnouncementTask> implements TaskFactory<T> {

    public static final String PREFIX = "announcement";

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

    public String getPrefix() {

        return PREFIX;
    }

    @Override
    public boolean hasNamedTask() {

        return true;
    }

    public TaskService getService() {

        return this.service;
    }

    public JDAStore getStore() {

        return this.store;
    }

    public AnimeService getAnimeService() {

        return this.animeService;
    }

    public InterestService getInterestService() {

        return this.interestService;
    }

    public Task queue(AnimeEntity<?> anime) {

        return this.queue(anime, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(AnimeEntity<?> anime, byte priority) {

        String       name      = String.format("%s:%s", this.getName(), anime.getId());
        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(AnnouncementTask.OPTION_ANIME, anime.getId());

        return this.service.queue(
                TaskBuilder.of(this)
                           .name(name)
                           .args(arguments)
                           .priority(priority)
        );
    }

}
