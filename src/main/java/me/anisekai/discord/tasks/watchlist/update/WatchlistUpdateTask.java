package me.anisekai.discord.tasks.watchlist.update;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.responses.embeds.WatchlistEmbed;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.entities.Watchlist;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.InterestService;
import me.anisekai.server.services.WatchlistService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.utils.DiscordUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.List;

public class WatchlistUpdateTask implements TaskExecutor {

    public static final String OPTION_WATCHLIST = "watchlist";

    private final AnimeService     animeService;
    private final InterestService  interestService;
    private final WatchlistService watchlistService;
    private final JDAStore         store;

    public WatchlistUpdateTask(AnimeService animeService, InterestService interestService, WatchlistService watchlistService, JDAStore store) {

        this.animeService     = animeService;
        this.interestService  = interestService;
        this.watchlistService = watchlistService;
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

        return params.has(OPTION_WATCHLIST);
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

        timer.action("load", "Loading task data");
        String         rawStatus = params.getString(OPTION_WATCHLIST);
        AnimeStatus    status    = AnimeStatus.valueOf(rawStatus.toUpperCase());
        Watchlist      watchlist = this.watchlistService.fetch(status);
        MessageChannel channel   = this.store.requireWatchlistChannel();
        Message message = DiscordUtils
                .findExistingMessage(channel, watchlist)
                .orElseThrow(() -> new IllegalStateException("Watchlist has most likely not been created yet."));

        List<Anime>    animes    = this.animeService.getOfStatus(status);
        List<Interest> interests = this.interestService.getInterests(animes);
        timer.endAction();

        timer.action("embed", "Creating embed");
        WatchlistEmbed embed = new WatchlistEmbed();
        embed.setWatchlistContent(status, animes, interests);
        timer.endAction();

        timer.action("update", "Updating the message");
        MessageEditBuilder meb = new MessageEditBuilder();
        meb.setEmbeds(embed.build());
        message.editMessage(meb.build()).complete();
        timer.endAction();
    }

}
