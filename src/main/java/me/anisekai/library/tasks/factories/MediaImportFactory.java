package me.anisekai.library.tasks.factories;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import jakarta.annotation.PostConstruct;
import me.anisekai.library.LibraryService;
import me.anisekai.library.tasks.executors.MediaImportTask;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.services.TorrentFileService;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.services.TrackService;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class MediaImportFactory implements TaskFactory<MediaImportTask> {

    public static final String NAME = "MEDIA:IMPORT";

    private final TaskService        service;
    private final LibraryService     libraryService;
    private final TrackService       trackService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;


    public MediaImportFactory(TaskService service, LibraryService libraryService, TorrentService torrentService, TrackService trackService, TorrentFileService torrentFileService) {

        this.service            = service;
        this.libraryService     = libraryService;
        this.torrentService     = torrentService;
        this.trackService       = trackService;
        this.torrentFileService = torrentFileService;
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
                this.torrentFileService
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
