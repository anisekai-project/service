package fr.anisekai.library.tasks.factories;

import fr.anisekai.library.LibraryService;
import fr.anisekai.library.tasks.executors.TorrentRetentionControlTask;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.enums.TaskPipeline;
import fr.anisekai.server.services.SettingService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.server.services.TorrentFileService;
import fr.anisekai.server.services.TorrentService;
import fr.anisekai.server.tasking.TaskBuilder;
import fr.anisekai.server.tasking.TaskFactory;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TorrentRetentionControlFactory implements TaskFactory<TorrentRetentionControlTask> {

    public static final String NAME = "torrent:cleanup";

    private final TaskService        service;
    private final SettingService     settingService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;
    private final LibraryService     libraryService;

    public TorrentRetentionControlFactory(TaskService service, SettingService settingService, TorrentService torrentService, TorrentFileService torrentFileService, LibraryService libraryService) {

        this.service            = service;
        this.settingService     = settingService;
        this.torrentService     = torrentService;
        this.torrentFileService = torrentFileService;
        this.libraryService     = libraryService;
    }

    @Override
    public @NotNull String getName() {

        return NAME;
    }

    @Override
    public @NotNull TorrentRetentionControlTask create() {

        return new TorrentRetentionControlTask(
                this.settingService,
                this.torrentService,
                this.torrentFileService,
                this.libraryService
        );
    }

    @Override
    public boolean hasNamedTask() {

        return false;
    }

    public Task queue() {

        return this.queue(Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(byte priority) {

        return this.service.queue(
                TaskBuilder.of(this)
                           .priority(priority)
        );
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(TaskPipeline.SOFT, this);
    }

}
