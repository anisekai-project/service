package me.anisekai.discord.tasks.broadcast.cleaning;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.tasking.TaskExecutor;

public class BroadcastCleaningTask implements TaskExecutor {

    private final BroadcastService service;
    private final JDAStore         store;

    public BroadcastCleaningTask(BroadcastService service, JDAStore store) {

        this.service = service;
        this.store   = store;
    }

    /**
     * Run this task.
     *
     * @param timer
     *         The timer to use to mesure performance of the task.
     * @param params
     *         The parameters of this task.
     *
     * @throws Exception
     *         Thew if something happens.
     */
    @Override
    public void execute(ITimedAction timer, BookshelfJson params) throws Exception {
        // TODO
        throw new IllegalAccessException("Task not implemented yet");
    }

}
