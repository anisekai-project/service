package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.WatchlistUpdatedEvent;
import me.anisekai.toshiko.helpers.JDAStore;
import me.anisekai.toshiko.helpers.containers.VariablePair;
import me.anisekai.toshiko.repositories.WatchlistRepository;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Service
public class WatchlistTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistTask.class);

    @Value("${toshiko.anime.server}")
    private long toshikoAnimeServer;

    @Value("${toshiko.anime.notification.channel}")
    private long toshikoAnimeNotificationChannel;

    @Value("${toshiko.anime.notification.role}")
    private long toshikoAnimeNotificationRole;

    @Value("${toshiko.anime.watchlist.channel}")
    private long toshikoAnimeWatchlistChannel;

    private final JDAStore                   store;
    private final AnimeService               service;
    private final WatchlistRepository        repository;
    private final BlockingDeque<AnimeStatus> statuses;

    public WatchlistTask(JDAStore store, AnimeService service, WatchlistRepository repository) {

        this.store      = store;
        this.service    = service;
        this.repository = repository;
        this.statuses   = new LinkedBlockingDeque<>();
    }

    @EventListener(WatchlistUpdatedEvent.class)
    public void onWatchlistUpdateDetected(WatchlistUpdatedEvent event) {

        if (!this.statuses.contains(event.getStatus())) {
            this.statuses.add(event.getStatus());
        }
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void execute() {

        AnimeStatus status = this.statuses.poll();

        if (status == null || !status.shouldDisplayList()) {
            return;
        }

        Optional<Watchlist> optionalWatchlist = this.repository.findById(status);
        EmbedBuilder        builder           = new EmbedBuilder();

        Message   message;
        Watchlist watchlist;

        if (optionalWatchlist.isEmpty()) {
            LOGGER.info("Creating message for watchlist {}", status.name());
            message   = this.createNewMessage();
            watchlist = new Watchlist(status, message);
        } else {
            watchlist = optionalWatchlist.get();
            Optional<Message> existingMessage = this.findExistingMessage(watchlist);

            if (existingMessage.isEmpty()) {
                LOGGER.info("Creating message for watchlist {}", status.name());
                message   = this.createNewMessage();
                watchlist = new Watchlist(status, message);
            } else {
                message = existingMessage.get();
            }
        }

        LOGGER.info("Updating message for watchlist {}", status.name());
        this.updateExistingMessage(watchlist, message);
        this.repository.save(watchlist);

        TextChannel channel = this.getTextChannel();
        TextChannelManager manager = channel.getManager();
        manager.setTopic(String.format("Il y a en tout %s animes", this.service.getDisplayableCount())).complete();
    }

    private TextChannel getTextChannel() {

        return this.store.getInstance()
                         .map(jda -> jda.getTextChannelById(this.toshikoAnimeWatchlistChannel))
                         .orElseThrow(() -> new IllegalStateException("Wololo, le channel même qu'il est pas trouvéééééééé"));
    }

    private Message createNewMessage() {

        TextChannel channel = this.getTextChannel();
        return channel.sendMessageEmbeds(new EmbedBuilder().setDescription("Isekai en cours...").build()).complete();
    }

    private void updateExistingMessage(Watchlist watchlist, Message message) {

        this.displayGenericEmbed(watchlist, message);
    }

    private void displayGenericEmbed(Watchlist watchlist, Message message) {

        EmbedBuilder builder = new EmbedBuilder();
        List<Anime>  animes  = this.service.findAllByStatus(watchlist.getStatus());
        LOGGER.info(" > {} animes found", animes.size());
        Map<Anime, Double> animeVotes = this.service.getAnimeVotes();

        builder.setAuthor(String.format("%s (%s)", watchlist.getStatus().getDisplay(), animes.size()));

        StringBuilder withLinks    = new StringBuilder();
        StringBuilder withoutLinks = new StringBuilder();

        for (Anime anime : animes) {

            VariablePair<String, String> result = DiscordUtils.buildAnimeList(this.service, anime);

            String entryWithLink = result.getFirst();
            String entryWithoutLink =result.getSecond();

            withLinks.append(entryWithLink).append("\n\n");
            withoutLinks.append(entryWithoutLink).append("\n\n");
        }

        if (withLinks.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
            builder.setDescription(withoutLinks);
        } else {
            builder.setDescription(withLinks);
        }

        builder.setFooter("Dernière actualisation le");
        builder.setTimestamp(LocalDateTime.now());

        LOGGER.info(" > Refresh...");
        message.editMessageEmbeds(builder.build()).complete();
    }

    private Optional<Message> findExistingMessage(Watchlist watchlist) {

        TextChannel channel = this.getTextChannel();
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
