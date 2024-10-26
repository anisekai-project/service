package me.anisekai.globals.tasking.tasks;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.tasks.commons.BroadcastTaskExecutor;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import me.anisekai.modules.toshiko.JdaStore;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastRemoveTaskExector extends BroadcastTaskExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(BroadcastRemoveTaskExector.class);

    private final BroadcastDataService broadcastService;
    private final JdaStore             store;

    public BroadcastRemoveTaskExector(BroadcastDataService broadcastService, JdaStore store) {

        this.broadcastService = broadcastService;
        this.store            = store;
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
        Broadcast      broadcast = this.broadcastService.fetch(params.getLong(OPT_BROADCAST));
        ScheduledEvent event     = this.requireEvent(guild, broadcast);
        timer.endAction();

        timer.action("remove", "Unschedule the event on Discord");
        LOGGER.info("Unschedule the event on Discord");
        if (event.getStatus() == ScheduledEvent.Status.ACTIVE) {
            LOGGER.info(" -> Completing event...");
            event.getManager().setStatus(ScheduledEvent.Status.COMPLETED).complete();
        } else {
            LOGGER.info(" -> Canceling event...");
            event.getManager().setStatus(ScheduledEvent.Status.CANCELED).complete();
        }
        timer.endAction();
    }

}
