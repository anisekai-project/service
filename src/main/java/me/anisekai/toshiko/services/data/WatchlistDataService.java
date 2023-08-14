package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.interfaces.entities.IWatchlist;
import me.anisekai.toshiko.modules.discord.JdaStore;
import me.anisekai.toshiko.modules.discord.tasks.WatchlistTask;
import me.anisekai.toshiko.repositories.WatchlistRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.TaskService;
import me.anisekai.toshiko.services.proxy.WatchlistProxyService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Service
public class WatchlistDataService extends AbstractDataService<Watchlist, AnimeStatus, IWatchlist, WatchlistRepository, WatchlistProxyService> {

    private final TaskService    taskService;
    private final RankingHandler rankingHandler;
    private final JdaStore       jdaStore;

    public WatchlistDataService(WatchlistProxyService proxy, TaskService taskService, RankingHandler rankingHandler, JdaStore jdaStore) {

        super(proxy);
        this.taskService    = taskService;
        this.rankingHandler = rankingHandler;
        this.jdaStore       = jdaStore;
    }

    public void refresh(Collection<Watchlist> statusList) {

        TextChannel channel = this.jdaStore.getWatchlistChannel();

        statusList
                .stream()
                .sorted()
                .map(watchlist -> new WatchlistTask(this, this.rankingHandler, watchlist, channel))
                .forEach(this.taskService::queue);
    }

    public void refreshAll() {

        this.refresh(this.fetchAll());
    }

    public void refresh(AnimeStatus status) {

        this.refresh(Collections.singletonList(this.fetch(status)));
    }

}
