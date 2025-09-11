package fr.anisekai.discord.tasks.anime.announcement.create;

import fr.anisekai.discord.JDAStore;
import fr.anisekai.discord.tasks.anime.announcement.AnnouncementFactory;
import fr.anisekai.server.enums.TaskPipeline;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.services.InterestService;
import fr.anisekai.server.services.TaskService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementCreateFactory extends AnnouncementFactory<AnnouncementCreateTask> {

    public static final String NAME = "create";

    public AnnouncementCreateFactory(TaskService service, JDAStore store, AnimeService animeService, InterestService interestService) {

        super(service, store, animeService, interestService);
    }

    @Override
    public @NotNull String getName() {

        return String.format("%s:%s", this.getPrefix(), NAME);
    }

    @Override
    public @NotNull AnnouncementCreateTask create() {

        return new AnnouncementCreateTask(this.getAnimeService(), this.getInterestService(), this.getStore());
    }

    @PostConstruct
    public void postConstruct() {

        this.getService().registerFactory(TaskPipeline.MESSAGING, this);
    }

}
