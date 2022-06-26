package me.anisekai.toshiko.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class DelayedTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedTask.class);

    private final BlockingDeque<Runnable> tasks;

    public DelayedTask() {

        this.tasks = new LinkedBlockingDeque<>();
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void execute() {

        Runnable poll = this.tasks.poll();
        if (poll != null) {
            LOGGER.info(" - Still {} tasks to execute", this.tasks.size());
            poll.run();
        }
    }

    public void queue(Runnable runnable) {

        this.tasks.offer(runnable);
    }
}
