package me.anisekai.toshiko.tasks;

import jakarta.annotation.PostConstruct;
import me.anisekai.toshiko.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;

@Service
public class AnimeDatabaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimeDatabaseTask.class);

    private final StorageService service;

    public AnimeDatabaseTask(StorageService service) {

        this.service = service;
    }

    @Scheduled(cron = "0 0/30 * * * *") // Every 30m
    public void execute() {

        LOGGER.info("Starting cache building...");

        ZonedDateTime start = ZonedDateTime.now();
        this.service.cache();
        ZonedDateTime end = ZonedDateTime.now();

        long duration = Duration.between(start, end).getSeconds();
        LOGGER.info("Cache built in {}s", duration);
    }

    @PostConstruct
    public void onServiceReady() {
        // Populate cache at least once.
        this.execute();
    }

}
