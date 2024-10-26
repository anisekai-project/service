package me.anisekai.modules.shizue.services;

import me.anisekai.modules.shizue.data.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Deprecated
public class RateLimitedTaskService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RateLimitedTaskService.class);

    private final BlockingDeque<String> taskQueue;
    private final Map<String, Task>     runnableMap;

    public RateLimitedTaskService() {

        this.taskQueue   = new LinkedBlockingDeque<>();
        this.runnableMap = new HashMap<>();
    }

    public void queue(Task task) {

        // This will replace existing value, ensuring up-to-date content.
        this.runnableMap.put(task.getName(), task);

        if (!this.taskQueue.contains(task.getName())) {
            LOGGER.info("The task '{}' has been queued.", task.getName());
            this.taskQueue.offer(task.getName());
        } else {
            LOGGER.info("The task '{}' has been updated.", task.getName());
        }
    }

    @Scheduled(cron = "0/6 * * * * *")
    private void execute() {

        String taskName = this.taskQueue.poll();

        if (taskName == null) {
            return;
        }

        if (!this.runnableMap.containsKey(taskName)) {
            LOGGER.warn("Could not execute task named '{}': The runnable was not found.", taskName);
            return;
        }

        LOGGER.debug("[{} left] Running '{}' task...", this.taskQueue.size(), taskName);
        long start = System.currentTimeMillis();
        Task task  = this.runnableMap.get(taskName);

        try {
            task.run();
            long end = System.currentTimeMillis();
            task.onFinished();
            LOGGER.debug("[{} left] Task '{}' executed in {}ms", this.taskQueue.size(), task.getName(), (end - start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            LOGGER.debug("[{} left] Task '{}' failed after {}ms", this.taskQueue.size(), task.getName(), (end - start));
            LOGGER.error("Task failed:", e);
            task.onException(e);
        }
    }

}
