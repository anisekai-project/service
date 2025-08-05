package fr.anisekai;

import fr.anisekai.discord.tasks.broadcast.cleaning.BroadcastCleaningFactory;
import fr.anisekai.library.tasks.executors.TorrentSourcingTask;
import fr.anisekai.library.tasks.factories.TorrentRetentionControlFactory;
import fr.anisekai.library.tasks.factories.TorrentSourcingFactory;
import fr.anisekai.library.tasks.factories.TorrentSynchronizationFactory;
import fr.anisekai.server.services.SettingService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.sentry.NoopTimedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CronTasks {

    private final static Logger LOGGER = LoggerFactory.getLogger(CronTasks.class);

    private final TaskService    service;
    private final SettingService settingService;

    public CronTasks(TaskService service, SettingService settingService) {

        this.service        = service;
        this.settingService = settingService;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void runCleaning() throws Exception {

        this.service.getFactory(BroadcastCleaningFactory.class)
                    .create()
                    .execute(new NoopTimedAction(), new AnisekaiJson());

        this.service.getFactory(TorrentRetentionControlFactory.class)
                    .create()
                    .execute(new NoopTimedAction(), new AnisekaiJson());
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void runTorrentSync() throws Exception {

        if (!this.settingService.isDownloadEnabled()) {
            return;
        }

        Optional<String> optionalServer = this.settingService.getDownloadServer();

        if (optionalServer.isEmpty()) {
            return;
        }

        this.service.getFactory(TorrentSynchronizationFactory.class)
                    .create()
                    .execute(new NoopTimedAction(), new AnisekaiJson());
    }

    @Scheduled(cron = "0 */15 * * * *")
    private void runTorrentSourcing() throws Exception {

        if (!this.settingService.isDownloadEnabled()) {
            return;
        }

        Optional<String> optionalSource = this.settingService.getDownloadSource();
        Optional<String> optionalServer = this.settingService.getDownloadServer();

        if (optionalSource.isEmpty()) {
            LOGGER.warn("No download source available");
            return;
        }

        if (optionalServer.isEmpty()) {
            LOGGER.warn("No download server available");
            return;
        }

        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(TorrentSourcingTask.OPTION_SOURCE, optionalSource.get());

        this.service.getFactory(TorrentSourcingFactory.class)
                    .create()
                    .execute(new NoopTimedAction(), arguments);
    }

}
