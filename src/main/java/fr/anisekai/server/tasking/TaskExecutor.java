package fr.anisekai.server.tasking;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.server.exceptions.task.TaskArgumentException;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.json.validation.JsonObjectRule;
import org.json.JSONException;

public interface TaskExecutor {

    String OPTION_PRIORITY = "priority";

    /**
     * Check if the executor can find the required content in the provide {@link AnisekaiJson} for its execution.
     *
     * @param params
     *         A {@link AnisekaiJson}
     *
     * @throws TaskArgumentException
     *         Thrown when the validation fails
     */
    default void validateParams(AnisekaiJson params) throws TaskArgumentException {

        params.validate(
                new JsonObjectRule(OPTION_PRIORITY, true, Integer.class, int.class)
        );
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
