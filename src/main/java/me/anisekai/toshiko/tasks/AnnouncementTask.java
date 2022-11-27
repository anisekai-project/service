package me.anisekai.toshiko.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeUpdateType;
import me.anisekai.toshiko.events.AnimeUpdateEvent;
import me.anisekai.toshiko.helpers.JdaStoreService;
import me.anisekai.toshiko.helpers.embeds.AnimeEmbed;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

@Service
public class AnnouncementTask {

    private static final Logger                          LOGGER = LoggerFactory.getLogger(AnnouncementTask.class);
    private final        JdaStoreService                 store;
    private final        DelayedTask                     delayedTask;
    private final        ToshikoService                  toshikoService;
    private final        BlockingDeque<AnimeUpdateEvent> notificationQueue;
    @Value("${toshiko.anime.notification.channel}")
    private              long                            toshikoAnimeNotificationChannel;
    @Value("${toshiko.anime.notification.role}")
    private              long                            toshikoAnimeNotificationRole;

    public AnnouncementTask(JdaStoreService store, DelayedTask delayedTask, ToshikoService toshikoService) {

        this.store             = store;
        this.delayedTask       = delayedTask;
        this.toshikoService    = toshikoService;
        this.notificationQueue = new LinkedBlockingDeque<>();
    }

    @EventListener(AnimeUpdateEvent.class)
    public void onNewAnimeAdded(AnimeUpdateEvent event) {

        this.notificationQueue.offer(event);
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void execute() {

        AnimeUpdateEvent event = this.notificationQueue.poll();

        if (event == null) {
            return;
        }

        try {
            Anime anime = this.toshikoService.reload(event.getAnime());

            Optional<Message> existingMessage;

            if (anime.getAnnounceMessage() != null && anime.getAnnounceMessage() == -1) {
                existingMessage = Optional.empty();
            } else {
                existingMessage = this.findExistingMessage(anime);
            }

            LOGGER.info(
                    "Handling notification for anime {} ({}): {}",
                    anime.getId(),
                    anime.getName(),
                    event.getType().name()
            );

            if (event.getType().shouldNotify() || event.getType() == AnimeUpdateType.REMOVE) {
                existingMessage.ifPresent(msg -> {
                    LOGGER.info("Removing existing announce for anime {} ({})", anime.getId(), anime.getName());
                    msg.delete().complete();
                });

                if (event.getType() == AnimeUpdateType.REMOVE) {
                    return;
                }

                TextChannel channel = this.getTextChannel();

                Runnable runnable = () -> {
                    Anime reloadedAnime = this.toshikoService.reload(anime);
                    LOGGER.info("Sending announce for anime {} ({})", reloadedAnime.getId(), reloadedAnime.getName());
                    MessageCreateBuilder createBuilder = new MessageCreateBuilder();
                    this.getMessage(reloadedAnime, event.getType()).accept(createBuilder);
                    Message sentMessage = channel.sendMessage(createBuilder.build()).complete();
                    this.toshikoService.setAnimeAnnouncementMessage(reloadedAnime, sentMessage);
                };

                this.delayedTask.queue(String.format("ANNOUNCEMENT NOTIFY " + anime.getId()), runnable, (ex) -> {
                    LOGGER.error("Unable to send announcement", ex);
                    Sentry.captureException(ex);
                });
                return;
            }

            existingMessage.ifPresent(msg -> {
                Runnable runnable = () -> {
                    Anime reloadedAnime = this.toshikoService.reload(anime);
                    LOGGER.info("Updating announce for anime {} ({})", reloadedAnime.getId(), reloadedAnime.getName());
                    MessageEditBuilder editBuilder = MessageEditBuilder.fromMessage(msg);
                    this.getMessage(reloadedAnime, msg.getContentRaw()).accept(editBuilder);
                    msg.editMessage(editBuilder.build()).complete();
                };

                this.delayedTask.queue(String.format("ANNOUNCEMENT REFRESH " + anime.getId()), runnable);
            });

        } catch (Exception e) {

            this.notificationQueue.offer(event);
        }
    }

    private Consumer<MessageRequest<?>> getMessage(Anime anime, AnimeUpdateType type) {

        String content;

        if (type == AnimeUpdateType.ADDED) {
            String notificationText = "Hey %s ! Un nouvel anime a été ajouté par %s !";
            String roleMention      = this.getRole().getAsMention();
            String userMention      = UserSnowflake.fromId(anime.getAddedBy().getId()).getAsMention();
            content = notificationText.formatted(roleMention, userMention);
        } else if (type == AnimeUpdateType.RELEASING) {
            String notificationText = "Hey %s ! Un anime est désormais disponible en simulcast !";
            String roleMention      = this.getRole().getAsMention();
            content = notificationText.formatted(roleMention);
        } else {
            String notificationText = "Hey %s ! Un anime est désormais disponible !";
            String roleMention      = this.getRole().getAsMention();
            content = notificationText.formatted(roleMention);
        }

        return this.getMessage(anime, content);
    }

    private Consumer<MessageRequest<?>> getMessage(Anime anime, String content) {

        AnimeEmbed message = new AnimeEmbed(anime, 0.0);
        message.setContent(content);
        message.setShowButtons(true);
        return message.getHandler();
    }


    private Optional<Message> findExistingMessage(Anime anime) {

        if (anime.getAnnounceMessage() == null || anime.getAnnounceMessage() == -1) {
            return Optional.empty();
        }

        TextChannel channel = this.getTextChannel();
        try {
            return Optional.of(channel.retrieveMessageById(anime.getAnnounceMessage()).complete());
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                return Optional.empty();
            }
            throw e;
        }
    }

    private TextChannel getTextChannel() {

        return this.store.getInstance()
                         .map(jda -> jda.getTextChannelById(this.toshikoAnimeNotificationChannel))
                         .orElseThrow(() -> new IllegalStateException("Wololo, le channel même qu'il est pas trouvéééééééé"));
    }

    private Role getRole() {

        return this.store.getInstance()
                         .map(jda -> jda.getRoleById(this.toshikoAnimeNotificationRole))
                         .orElseThrow(() -> new IllegalStateException("Wololo, le role même qu'il est pas trouvéééééééé"));
    }
}
