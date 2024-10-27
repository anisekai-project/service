package me.anisekai.globals.tasking.tasks;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.responses.ButtonResponse;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.components.RankingHandler;
import me.anisekai.modules.shizue.entities.Watchlist;
import me.anisekai.modules.shizue.interfaces.entities.IWatchlist;
import me.anisekai.modules.shizue.services.data.WatchlistDataService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.messages.embeds.WatchlistEmbed;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WatchlistTaskExecutor implements TaskExecutor {

    public static final String OPT_STATUS = "status";

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistTaskExecutor.class);

    private final WatchlistDataService service;
    private final RankingHandler       ranking;
    private final JdaStore             store;

    public WatchlistTaskExecutor(WatchlistDataService service, RankingHandler ranking, JdaStore store) {

        this.service = service;
        this.ranking = ranking;
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

        return params.has(OPT_STATUS);
    }

    /**
     * Run this task.
     *
     * @param timer
     *         The timer to use to mesure performance of the task.
     * @param params
     *         The parameters of this task.
     */
    @Override
    public void execute(ITimedAction timer, BookshelfJson params) {

        timer.action("load-data", "Loading task data");
        LOGGER.info("Loading task data");
        String         rawStatus           = params.getString(OPT_STATUS);
        AnimeStatus    status              = AnimeStatus.valueOf(rawStatus.toUpperCase());
        Watchlist      watchlist           = this.service.fetch(status);
        TextChannel    channel             = this.store.getWatchlistChannel();
        boolean        shouldCreateMessage = watchlist.getMessageId() == null;
        ButtonResponse message             = new WatchlistEmbed(watchlist, this.ranking);
        timer.endAction();

        timer.action("find-message", "Looking for discord message");
        LOGGER.info("Looking for discord message");
        Optional<Message> existingMessage = this.findExistingMessage(channel, watchlist);
        timer.endAction();

        if (shouldCreateMessage || existingMessage.isEmpty()) {
            timer.action("send-message", "Sending a message in the watchlist channel");
            LOGGER.info("Sending a message in the watchlist channel");
            MessageCreateBuilder mcb = new MessageCreateBuilder();
            message.getHandler().accept(mcb);
            Message discordMessage = channel.sendMessage(mcb.build()).complete();
            timer.endAction();

            timer.action("update-entity", "Updating the entity in the database");
            LOGGER.info("Updating the entity in the database");
            this.service.mod(watchlist.getId(), w -> w.setMessageId(discordMessage.getIdLong()));
            timer.endAction();

        } else {
            timer.action("update-message", "Update a message in the watchlist channel");
            LOGGER.info("Update a message in the watchlist channel");
            MessageEditBuilder meb = new MessageEditBuilder();
            message.getHandler().accept(meb);
            existingMessage.get().editMessage(meb.build()).complete();
            timer.endAction();
        }
    }


    private Optional<Message> findExistingMessage(MessageChannel channel, IWatchlist watchlist) {

        if (watchlist.getMessageId() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(channel.retrieveMessageById(watchlist.getMessageId()).complete());
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                return Optional.empty();
            }
            throw e;
        }
    }

}
