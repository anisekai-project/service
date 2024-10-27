package me.anisekai.globals.tasking.tasks;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.utils.EmbeddingUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageLogTaskExecutor implements TaskExecutor {

    public static final String EMBED = "embed";

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageLogTaskExecutor.class);

    private final JdaStore store;

    public MessageLogTaskExecutor(JdaStore store) {

        this.store = store;
    }

    /**
     * Check if the executor can find the required content in the provide {@link BookshelfJson} for its execution.
     *
     * @param params
     *         A {@link BookshelfJson}
     *
     * @return True if the json contains all settings, false otherwise.
     */
    @Override
    public boolean validateParams(BookshelfJson params) {

        return params.has(EMBED);
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

        BookshelfJson jsonEmbed    = new BookshelfJson(params.getJSONObject(EMBED).toMap());
        MessageEmbed  messageEmbed = EmbeddingUtils.fromJson(jsonEmbed);

        this.store.getAuditLogChannel()
                  .sendMessageEmbeds(messageEmbed)
                  .complete();
    }

}
