package me.anisekai.library.tasks.factories;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import jakarta.annotation.PostConstruct;
import me.anisekai.library.tasks.executors.TorrentSourcingTask;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.*;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TorrentSourcingFactory implements TaskFactory<TorrentSourcingTask> {

    private static final String             NAME = "torrent:sourcing";
    private final        TaskService        service;
    private final        AnimeService       animeService;
    private final        EpisodeService     episodeService;
    private final        TorrentService     torrentService;
    private final        TorrentFileService torrentFileService;

    public TorrentSourcingFactory(TaskService service, AnimeService animeService, EpisodeService episodeService, TorrentService torrentService, TorrentFileService torrentFileService) {

        this.service            = service;
        this.animeService       = animeService;
        this.episodeService     = episodeService;
        this.torrentService     = torrentService;
        this.torrentFileService = torrentFileService;
    }

    @Override
    public @NotNull String getName() {

        return NAME;
    }

    @Override
    public @NotNull TorrentSourcingTask create() {

        return new TorrentSourcingTask(
                this.animeService,
                this.episodeService,
                this.torrentService,
                this.torrentFileService
        );
    }

    @Override
    public boolean hasNamedTask() {

        return false;
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

    public Task queue(String source) {

        return this.queue(source, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(String source, byte priority) {

        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(TorrentSourcingTask.OPTION_SOURCE, source);
        return this.service.queue(this, this.getName(), arguments, priority);
    }

}
