package me.anisekai;

import me.anisekai.discord.tasks.broadcast.cleaning.BroadcastCleaningFactory;
import me.anisekai.library.tasks.factories.TorrentSourcingFactory;
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
    public void runBroadcastCleaning() {

        this.service.getFactory(BroadcastCleaningFactory.class).queue();
    }

    @Scheduled(cron = "0 */15 * * * *")
    private void runTorrentSourcing() {

        Optional<String> optionalSource = this.settingService.getDownloadSource();
        if (optionalSource.isEmpty()) {
            LOGGER.warn("No download source available");
            return;
        }

        String source = optionalSource.get();
        this.service.getFactory(TorrentSourcingFactory.class).queue(source);
    }

}
