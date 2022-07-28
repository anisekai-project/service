package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeUpdateType;
import me.anisekai.toshiko.events.AnimeUpdateEvent;
import me.anisekai.toshiko.helpers.JDAStore;
import me.anisekai.toshiko.helpers.embeds.AnimeSheetMessage;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.services.AnimeService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class AnnouncementTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementTask.class);
    private final JDAStore                        store;
    private final DelayedTask                     delayedTask;
    private final AnimeService                    service;
    private final AnimeRepository                 repository;
    private final BlockingDeque<AnimeUpdateEvent> notificationQueue;
    @Value("${toshiko.anime.server}")
    private long toshikoAnimeServer;
    @Value("${toshiko.anime.notification.channel}")
    private long toshikoAnimeNotificationChannel;
    @Value("${toshiko.anime.notification.role}")
    private long toshikoAnimeNotificationRole;
    @Value("${toshiko.anime.watchlist.channel}")
    private long toshikoAnimeWatchlistChannel;

    public AnnouncementTask(JDAStore store, DelayedTask delayedTask, AnimeService service, AnimeRepository repository) {

        this.store             = store;
        this.delayedTask       = delayedTask;
        this.service           = service;
        this.repository        = repository;
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
            Anime             anime = event.getAnime();
            Optional<Message> existingMessage;

            if (anime.getAnnounceMessage() != null && anime.getAnnounceMessage() == -1) {
                existingMessage = Optional.empty();
            } else {
                existingMessage = this.findExistingMessage(anime);
            }

            LOGGER.info("Handling notification for anime {} ({}): {}", anime.getId(), anime.getName(), event.getType()
                                                                                                            .name());

            if (event.getType().shouldNotify()) {
                existingMessage.ifPresent(msg -> msg.delete().complete());
                LOGGER.info("Sending announce for anime {} ({})", anime.getId(), anime.getName());
                Message message = this.getMessage(anime, event.getType());

                TextChannel channel = this.getTextChannel();

                Runnable runnable = () -> {
                    Message sentMessage = channel.sendMessage(message).complete();
                    anime.setAnnounceMessage(sentMessage.getIdLong());
                    this.repository.save(anime);
                };
                this.delayedTask.queue(String.format("ANNOUNCEMENT NOTIFY " + anime.getId()), runnable);

                return;
            }

            existingMessage.ifPresent(msg -> {
                LOGGER.info("Updating announce for anime {} ({})", anime.getId(), anime.getName());

                Runnable runnable = () -> msg.editMessage(this.getMessage(anime, msg.getContentRaw())).complete();
                this.delayedTask.queue(String.format("ANNOUNCEMENT REFRESH " + anime.getId()), runnable);
            });

            TextChannel        channel = this.getTextChannel();
            TextChannelManager manager = channel.getManager();
            this.delayedTask.queue("ANIME COUNT REFRESH", () -> manager.setTopic(
                    String.format(
                            "Il y a en tout %s animes",
                            this.service.getDisplayableCount()
                    )
            ).complete());

        } catch (Exception e) {

            this.notificationQueue.offer(event);
        }
    }

    private Message getMessage(Anime anime, AnimeUpdateType type) {

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

    private Message getMessage(Anime anime, String content) {

        AnimeSheetMessage message = new AnimeSheetMessage(anime, this.service.getInterests(anime));
        return message.getMessage(true, content);
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
