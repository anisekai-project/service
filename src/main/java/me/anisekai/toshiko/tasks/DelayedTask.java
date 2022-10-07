package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.tasks.entity.TaskEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

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
            LOGGER.info("TASK {} | Executing... ({} tasks left)", poll.getName(), this.tasks.size());
            try {
                poll.getRunnable().run();
                LOGGER.info("TASK {} | Success. ({} tasks left)", poll.getName(), this.tasks.size());
            } catch (Exception e) {
                LOGGER.warn("TASK {} | Failure. ({} tasks left)", poll.getName(), this.tasks.size());
                poll.getFailed().accept(e);
            }
        }
    }

    public void queue(String name, Runnable runnable) {

        this.tasks.offer(new TaskEntry(name, runnable));
        LOGGER.info("Runnable '{}' added; There are {} items in the queue.", name, this.tasks.size());
    }

    public void queue(String name, Runnable runnable, Consumer<Exception> failed) {

        this.tasks.offer(new TaskEntry(name, runnable, failed));
        LOGGER.info("Runnable '{}' added; There are {} items in the queue.", name, this.tasks.size());
    }


}
