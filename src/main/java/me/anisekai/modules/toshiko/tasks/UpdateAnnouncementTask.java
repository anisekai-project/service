package me.anisekai.modules.toshiko.tasks;

import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.shizue.data.Task;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.messages.embeds.AnimeEmbed;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UpdateAnnouncementTask implements Task {

    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateAnnouncementTask.class);

    private final TextChannel channel;
    private final Role        role;
    private final Anime       anime;

    public UpdateAnnouncementTask(JdaStore store, Anime anime) {

        this.channel = store.getAnnouncementChannel();
        this.role    = store.getAnnouncementRole();
        this.anime   = anime;
    }

    @Override
    public String getName() {

        return String.format("ANIME:%s:ANNOUNCE:UPDATE", this.anime.getId());
    }

    @Override
    public void run() {

        if (this.anime.getAnnounceMessage() == null) {
            LOGGER.info("Ignored update announcement task for anime {}. Reason: This anime hasn't been announced.", this.anime.getId());
        }

        LOGGER.info("Handling update announcement task for anime {}...", this.anime.getId());

        LOGGER.debug("Retrieving announcement discord message...");
        Optional<Message> existingMessage = this.findExistingMessage(this.anime);

        if (existingMessage.isEmpty()) {
            LOGGER.error(
                    "Could not refresh announcement message: Message {} not found.",
                    this.anime.getAnnounceMessage()
            );
            return;
        }

        Message discordMessage = existingMessage.get();

        MessageEditBuilder meb     = new MessageEditBuilder();
        AnimeEmbed         message = new AnimeEmbed(this.anime, 0);

        message.setContent(String.format(
                "Hey %s ! Un anime est désormais disponible !",
                this.role.getAsMention()
        ));

        message.setShowButtons(true);
        message.getHandler().accept(meb);

        LOGGER.debug("Updating announcement message...");
        discordMessage.editMessage(meb.build()).complete();
        LOGGER.info("Announcement task finished.");

    }

    private Optional<Message> findExistingMessage(IAnime anime) {

        if (anime.getAnnounceMessage() == null || anime.getAnnounceMessage() == -1) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.channel.retrieveMessageById(anime.getAnnounceMessage()).complete());
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                return Optional.empty();
            }
            throw e;
        }
    }

}
