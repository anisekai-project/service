package fr.anisekai.library.tasks.factories;

import fr.anisekai.library.LibraryService;
import fr.anisekai.library.tasks.executors.TorrentRetentionControlTask;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.services.SettingService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.server.services.TorrentFileService;
import fr.anisekai.server.services.TorrentService;
import fr.anisekai.server.tasking.TaskFactory;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TorrentRetentionControlFactory implements TaskFactory<TorrentRetentionControlTask> {

    public static final String NAME = "torrent:cleanup";

    private final TaskService        taskService;
    private final SettingService     settingService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;
    private final LibraryService     libraryService;

    public TorrentRetentionControlFactory(TaskService taskService, SettingService settingService, TorrentService torrentService, TorrentFileService torrentFileService, LibraryService libraryService) {

        this.taskService        = taskService;
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

    @PostConstruct
    private void postConstruct() {

        this.taskService.registerFactory(this);
    }

    public Task queue() {

        return this.queue(Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(byte priority) {

        AnisekaiJson arguments = new AnisekaiJson();
        return this.taskService.queue(this, this.getName(), arguments, priority);
    }


}
