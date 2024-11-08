package me.anisekai.modules.shizue.tasking.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.modules.shizue.tasking.BroadcastTaskExecutor;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import me.anisekai.modules.shizue.utils.BroadcastUtils;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.Texts;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

@SuppressWarnings("DuplicatedCode")
public class BroadcastUpdateTaskExecutor extends BroadcastTaskExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(BroadcastUpdateTaskExecutor.class);

    private final BroadcastDataService service;
    private final JdaStore             store;

    public BroadcastUpdateTaskExecutor(BroadcastDataService service, JdaStore store) {

        this.service = service;
        this.store   = store;
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

        return params.has(OPT_BROADCAST);
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

        timer.action("load-data", "Loading task data");
        LOGGER.info("Loading task data");
        Guild          guild     = this.store.getBotGuild();
        Broadcast      broadcast = this.service.fetch(params.getLong(OPT_BROADCAST));
        ScheduledEvent event     = this.requireEvent(guild, broadcast);
        Icon           icon      = this.getBroadcastImage(timer, broadcast);
        timer.endAction();

        timer.action("prepare", "Generating data for the event");
        LOGGER.info("Generating data for the event");
        String         name        = Texts.truncate(broadcast.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);
        String         description = BroadcastUtils.asEpisodeDescription(broadcast);
        OffsetDateTime startingAt  = broadcast.getStartingAt().toOffsetDateTime();
        OffsetDateTime endingAt    = broadcast.getEndingAt().toOffsetDateTime();
        timer.endAction();

        timer.action("updating", "Updating the event on Discord");
        event.getManager()
             .setName(name)
             .setDescription(description)
             .setImage(icon)
             .setStartTime(startingAt)
             .setEndTime(endingAt)
             .complete();
        timer.endAction();
    }

}
