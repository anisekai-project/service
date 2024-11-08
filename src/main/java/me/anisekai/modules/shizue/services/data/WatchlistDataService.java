package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.modules.toshiko.tasking.factories.WatchlistTaskFactory;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.entities.Watchlist;
import me.anisekai.modules.shizue.interfaces.entities.IWatchlist;
import me.anisekai.modules.shizue.repositories.WatchlistRepository;
import me.anisekai.modules.shizue.services.proxy.WatchlistProxyService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class WatchlistDataService extends DataService<Watchlist, AnimeStatus, IWatchlist, WatchlistRepository, WatchlistProxyService> {

    private final TaskingService taskingService;

    public WatchlistDataService(WatchlistProxyService proxy, TaskingService taskingService) {

        super(proxy);
        this.taskingService = taskingService;
    }

    public void refresh(Collection<Watchlist> statusList) {

        statusList
                .stream()
                .sorted()
                .forEach(status -> WatchlistTaskFactory.queue(this.taskingService, status.getId()));
    }

    public void refreshAll() {

        this.refresh(this.fetchAll());
    }

    public void createAll() {

        AnimeStatus.getDisplayable()
                   .stream()
                   .map(status -> this.getProxy().fetchEntity(status).orElseGet(() -> new Watchlist(status)))
                   .map(this.getProxy()::saveReload)
                   .forEach(status -> WatchlistTaskFactory.queue(this.taskingService, status.getId()));
    }

    public void refresh(AnimeStatus status) {

        this.refresh(Collections.singletonList(this.fetch(status)));
    }

}
