package fr.anisekai.library.tasks.factories;

import fr.anisekai.server.services.*;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import jakarta.annotation.PostConstruct;
import fr.anisekai.library.LibraryService;
import fr.anisekai.library.tasks.executors.MediaImportTask;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class MediaImportFactory implements TaskFactory<MediaImportTask> {

    public static final String NAME = "media:import";

    private final TaskService        service;
    private final LibraryService     libraryService;
    private final TrackService       trackService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;
    private final EpisodeService     episodeService;


    public MediaImportFactory(TaskService service, LibraryService libraryService, TorrentService torrentService, TrackService trackService, TorrentFileService torrentFileService, EpisodeService episodeService) {

        this.service            = service;
        this.libraryService     = libraryService;
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
                this.libraryService,
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

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

    public Task queue(String torrent) {

        return this.queue(torrent, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(String torrent, byte priority) {

        String       name      = String.format("%s:%s", this.getName(), torrent.toUpperCase());
        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(MediaImportTask.OPTION_TORRENT, torrent);
        return this.service.queue(this, name, arguments, priority);
    }

}
