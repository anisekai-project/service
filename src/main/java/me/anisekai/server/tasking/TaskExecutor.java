package me.anisekai.server.tasking;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.wireless.api.json.AnisekaiJson;

public interface TaskExecutor {

    String OPTION_PRIORITY = "priority";

    /**
     * Check if the executor can find the required content in the provide {@link AnisekaiJson} for its execution.
     *
     * @param params
     *         A {@link AnisekaiJson}
     *
     * @return True if the json contains all settings, false otherwise.
     */
    default boolean validateParams(AnisekaiJson params) {

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
    void execute(ITimedAction timer, AnisekaiJson params) throws Exception;

}
