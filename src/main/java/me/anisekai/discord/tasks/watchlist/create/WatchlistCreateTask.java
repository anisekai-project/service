package me.anisekai.discord.tasks.watchlist.create;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.responses.embeds.WatchlistEmbed;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.entities.Watchlist;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.services.WatchlistService;
import me.anisekai.server.tasking.TaskExecutor;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.List;

public class WatchlistCreateTask implements TaskExecutor {

    private final JDAStore         store;
    private final WatchlistService service;
    private final AnimeService     animeService;
    private final InterestService  interestService;

    public WatchlistCreateTask(JDAStore store, WatchlistService service, AnimeService animeService, InterestService interestService) {

        this.store           = store;
        this.service         = service;
        this.animeService    = animeService;
        this.interestService = interestService;
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

        MessageChannel channel = this.store.requireWatchlistChannel();

        timer.action("reset", "Reset the watchlists");
        List<Watchlist> watchlists = this.service.reset();
        timer.endAction();

        timer.action("send", "Send the watchlists on Discord");
        for (Watchlist watchlist : watchlists) {
            timer.action(watchlist.getId().name(), "Handle watchlist");

            timer.action("load", "Load the watchlist data");
            List<Anime>    animes    = this.animeService.getOfStatus(watchlist.getId());
            List<Interest> interests = this.interestService.getInterests(animes);
            timer.endAction();

            timer.action("embed", "Creating embed");
            WatchlistEmbed embed = new WatchlistEmbed();
            embed.setWatchlistContent(watchlist.getId(), animes, interests);
            timer.endAction();

            timer.action("update", "Sending the message");
            MessageCreateBuilder mcb = new MessageCreateBuilder();
            mcb.setEmbeds(embed.build());
            channel.sendMessage(mcb.build()).complete();
            timer.endAction();
        }
        timer.endAction();

    }

}
