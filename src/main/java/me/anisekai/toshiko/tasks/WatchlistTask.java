package me.anisekai.toshiko.tasks;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.CronState;
import me.anisekai.toshiko.helpers.JDAStore;
import me.anisekai.toshiko.helpers.embeds.WatchlistEmbed;
import me.anisekai.toshiko.repositories.WatchlistRepository;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WatchlistTask {

    private static final Logger              LOGGER = LoggerFactory.getLogger(WatchlistTask.class);
    private final        JDAStore            store;
    private final DelayedTask         delayedTask;
    private final ToshikoService      service;
    private final WatchlistRepository repository;
    @Value("${toshiko.anime.watchlist.channel}")
    private              long                toshikoAnimeWatchlistChannel;

    public WatchlistTask(JDAStore store, DelayedTask delayedTask, ToshikoService service, WatchlistRepository repository) {

        this.store       = store;
        this.delayedTask = delayedTask;
        this.service     = service;
        this.repository  = repository;
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void execute() {

        List<Watchlist> items = this.repository.findAll()
                                               .stream()
                                               .filter(w -> w.getState() == CronState.REQUIRED)
                                               .sorted().toList();

        if (items.isEmpty()) {
            return;
        }

        Map<Anime, Double> animeVotes = this.service.getAnimeVotes();

        for (Watchlist watchlist : items) {
            watchlist.setState(CronState.QUEUED);
            this.repository.save(watchlist);

            this.delayedTask.queue(String.format("WL:%s", watchlist.getStatus().name()), () -> {
                boolean           requireMessage  = watchlist.getMessageId() == null;
                WatchlistEmbed    embed           = new WatchlistEmbed(watchlist, animeVotes);
                Optional<Message> existingMessage = this.findExistingMessage(watchlist);

                if (requireMessage || existingMessage.isEmpty()) {
                    MessageCreateBuilder createBuilder = new MessageCreateBuilder();
                    embed.getHandler().accept(createBuilder);
                    Message message = this.getTextChannel().sendMessage(createBuilder.build()).complete();
                    watchlist.setState(CronState.DONE);
                    watchlist.setMessageId(message.getIdLong());
                    this.repository.save(watchlist);
                } else {
                    MessageEditBuilder editBuilder = new MessageEditBuilder();
                    embed.getHandler().accept(editBuilder);
                    existingMessage.get().editMessage(editBuilder.build()).complete();
                    watchlist.setState(CronState.DONE);
                    this.repository.save(watchlist);
                }
            }, (ex) -> {
                LOGGER.error("An error occurred.", ex);
                watchlist.setState(CronState.REQUIRED);
                this.repository.save(watchlist);
            });
        }

        this.delayedTask.queue("ANIME_COUNT", () -> {
            this.getTextChannel().getManager().setTopic(String.format(
                    "Il y a en tout %s animes",
                    this.service.getDisplayableAnimeCount()
            )).complete();
        }, (ex) -> LOGGER.error("Unable to execute ANIME_COUNT", ex));
    }

    private TextChannel getTextChannel() {

        return this.store.getInstance()
                         .map(jda -> jda.getTextChannelById(this.toshikoAnimeWatchlistChannel))
                         .orElseThrow(() -> new IllegalStateException("Wololo, le channel même qu'il est pas trouvéééééééé"));
    }

    private Optional<Message> findExistingMessage(Watchlist watchlist) {

        if (watchlist.getMessageId() == null) {
            return Optional.empty();
        }

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
