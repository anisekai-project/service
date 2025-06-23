package fr.anisekai.library.tasks.factories;

import fr.anisekai.library.Library;
import fr.anisekai.library.tasks.executors.MediaImportTask;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.enums.TaskPipeline;
import fr.anisekai.server.services.*;
import fr.anisekai.server.tasking.TaskBuilder;
import fr.anisekai.server.tasking.TaskFactory;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class MediaImportFactory implements TaskFactory<MediaImportTask> {

    public static final String NAME = "media:import";

    private final TaskService        service;
    private final Library            library;
    private final TrackService       trackService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;
    private final EpisodeService     episodeService;


    public MediaImportFactory(TaskService service, Library library, TorrentService torrentService, TrackService trackService, TorrentFileService torrentFileService, EpisodeService episodeService) {

        this.service            = service;
        this.library            = library;
        this.torrentService     = torrentService;
        this.trackService       = trackService;
        this.torrentFileService = torrentFileService;
        this.episodeService     = episodeService;
    }

    @Override
    public @NotNull String getName() {

        return NAME;
    }

    @Override
    public @NotNull MediaImportTask create() {

        return new MediaImportTask(
                this.library,
                this.trackService,
                this.torrentService,
                this.torrentFileService,
                this.episodeService
        );
    }

    @Override
    public boolean hasNamedTask() {

        return true;
    }

    public Task queue(String torrent) {

        return this.queue(torrent, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(String torrent, byte priority) {

        String       name      = String.format("%s:%s", this.getName(), torrent.toUpperCase());
        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(MediaImportTask.OPTION_TORRENT, torrent);

        return this.service.queue(
                TaskBuilder.of(this)
                           .name(name)
                           .args(arguments)
                           .priority(priority)
        );
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(TaskPipeline.HEAVY, this);
    }

}
