package me.anisekai.server.tasking;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;

public interface TaskExecutor {

    String OPTION_PRIORITY = "priority";

    /**
     * Check if the executor can find the required content in the provide {@link BookshelfJson} for its execution.
     *
     * @param params
     *         A {@link BookshelfJson}
     *
     * @return True if the json contains all settings, false otherwise.
     */
    default boolean validateParams(BookshelfJson params) {

        return true;
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
    void execute(ITimedAction timer, BookshelfJson params) throws Exception;

}
