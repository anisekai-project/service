package me.anisekai;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.ext.sentry.NoopTimedAction;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import me.anisekai.discord.tasks.broadcast.cleaning.BroadcastCleaningFactory;
import me.anisekai.discord.tasks.broadcast.cleaning.BroadcastCleaningTask;
import me.anisekai.library.tasks.executors.TorrentSourcingTask;
import me.anisekai.library.tasks.factories.TorrentSourcingFactory;
import me.anisekai.library.tasks.factories.TorrentSynchronizationFactory;
import me.anisekai.server.services.SettingService;
import me.anisekai.server.services.TaskService;
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
    public void runBroadcastCleaning() throws Exception {

        this.service.getFactory(BroadcastCleaningFactory.class)
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
