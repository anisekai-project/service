package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.components.RankingHandler;
import me.anisekai.modules.shizue.entities.Watchlist;
import me.anisekai.modules.shizue.interfaces.entities.IWatchlist;
import me.anisekai.modules.shizue.repositories.WatchlistRepository;
import me.anisekai.modules.shizue.services.RateLimitedTaskService;
import me.anisekai.modules.shizue.services.proxy.WatchlistProxyService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.tasks.WatchlistTask;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class WatchlistDataService extends DataService<Watchlist, AnimeStatus, IWatchlist, WatchlistRepository, WatchlistProxyService> {

    private final RateLimitedTaskService rateLimitedTaskService;
    private final RankingHandler         rankingHandler;
    private final JdaStore               jdaStore;

    public WatchlistDataService(WatchlistProxyService proxy, RateLimitedTaskService rateLimitedTaskService, RankingHandler rankingHandler, JdaStore jdaStore) {

        super(proxy);
        this.rateLimitedTaskService = rateLimitedTaskService;
        this.rankingHandler         = rankingHandler;
        this.jdaStore               = jdaStore;
    }

    public void refresh(Collection<Watchlist> statusList) {

        TextChannel channel = this.jdaStore.getWatchlistChannel();

        statusList
                .stream()
                .sorted()
                .map(watchlist -> new WatchlistTask(this, this.rankingHandler, watchlist, channel))
                .forEach(this.rateLimitedTaskService::queue);
    }

    public void refreshAll() {

        this.refresh(this.fetchAll());
    }

    public void refresh(AnimeStatus status) {

        this.refresh(Collections.singletonList(this.fetch(status)));
    }

}
