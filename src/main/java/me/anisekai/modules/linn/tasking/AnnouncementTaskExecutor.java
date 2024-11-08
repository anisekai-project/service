package me.anisekai.modules.linn.tasking;

import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.modules.linn.interfaces.IAnime;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AnnouncementTaskExecutor implements TaskExecutor {

    public static final  String OPT_ANIME = "anime";
    private static final Logger LOGGER    = LoggerFactory.getLogger(AnnouncementTaskExecutor.class);

    public Optional<Message> findExistingMessage(MessageChannel channel, IAnime anime) {

        if (anime.getAnnounceMessage() == null || anime.getAnnounceMessage() == -1) {
            return Optional.empty();
        }

        try {
            return Optional.of(channel.retrieveMessageById(anime.getAnnounceMessage()).complete());
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                return Optional.empty();
            }
            throw e;
        }
    }

}
