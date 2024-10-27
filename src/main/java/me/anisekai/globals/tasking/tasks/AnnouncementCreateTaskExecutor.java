package me.anisekai.globals.tasking.tasks;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.tasks.commons.AnnouncementTaskExecutor;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.messages.embeds.AnimeEmbed;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
public class AnnouncementCreateTaskExecutor extends AnnouncementTaskExecutor {

    private static final Logger           LOGGER = LoggerFactory.getLogger(AnnouncementCreateTaskExecutor.class);
    private final        JdaStore         store;
    private final        AnimeDataService service;

    public AnnouncementCreateTaskExecutor(AnimeDataService service, JdaStore store) {

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

        return params.has(OPT_ANIME);
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
        Anime             anime           = this.service.fetch(params.getLong(OPT_ANIME));
        MessageChannel    channel         = this.store.getAnnouncementChannel();
        Role              role            = this.store.getAnnouncementRole();
        Optional<Message> optionalMessage = this.findExistingMessage(channel, anime);

        if (optionalMessage.isPresent()) {
            throw new IllegalStateException("A message already exists.");
        }
        timer.endAction();

        timer.action("prepare", "Preparing message");
        LOGGER.info("Preparing message");
        MessageCreateBuilder mcb   = new MessageCreateBuilder();
        AnimeEmbed           embed = new AnimeEmbed(anime, 0);

        embed.setContent(String.format(
                "Hey %s ! Un anime est désormais disponible !",
                role.getAsMention()
        ));

        embed.setShowButtons(true);
        embed.getHandler().accept(mcb);
        timer.endAction();

        timer.action("send", "Sending message");
        LOGGER.info("Sending message");
        Message message = channel.sendMessage(mcb.build()).complete();
        timer.endAction();

        timer.action("commit", "Commit data to the database");
        LOGGER.info("Commit data to the database");
        this.service.mod(anime.getId(), item -> item.setAnnounceMessage(message.getIdLong()));
        timer.endAction();
    }

}
