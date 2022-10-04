package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.tasks.entity.TaskEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class DelayedTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedTask.class);

    private final BlockingDeque<TaskEntry> tasks;

    public DelayedTask() {

        this.tasks = new LinkedBlockingDeque<>();
    }

    @Scheduled(cron = "0/6 * * * * *")
    public void execute() {

        TaskEntry poll = this.tasks.poll();
        if (poll != null) {
            LOGGER.info("Executing task {}... ({} tasks left)", poll.getName(), this.tasks.size());
            poll.getRunnable().run();
        }
    }

    public void queue(String name, Runnable runnable) {

        this.tasks.offer(new TaskEntry(name, runnable));
        LOGGER.info("Runnable '{}' added; There are {} items in the queue.", name, this.tasks.size());
    }
}
