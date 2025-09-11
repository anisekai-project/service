package fr.anisekai.discord.tasks.anime.announcement.update;

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
public class AnnouncementUpdateFactory extends AnnouncementFactory<AnnouncementUpdateTask> {

    public static final String NAME = "update";

    public AnnouncementUpdateFactory(TaskService service, JDAStore store, AnimeService animeService, InterestService interestService) {

        super(service, store, animeService, interestService);
    }

    @Override
    public @NotNull String getName() {

        return String.format("%s:%s", this.getPrefix(), NAME);
    }

    @Override
    public @NotNull AnnouncementUpdateTask create() {

        return new AnnouncementUpdateTask(this.getAnimeService(), this.getInterestService(), this.getStore());
    }

    @PostConstruct
    public void postConstruct() {

        this.getService().registerFactory(TaskPipeline.MESSAGING, this);
    }

}
