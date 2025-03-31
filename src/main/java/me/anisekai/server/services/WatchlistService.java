package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.Watchlist;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.events.WatchlistCreatedEvent;
import me.anisekai.server.interfaces.IWatchlist;
import me.anisekai.server.proxy.WatchlistProxy;
import me.anisekai.server.repositories.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WatchlistService extends DataService<Watchlist, AnimeStatus, IWatchlist, WatchlistRepository, WatchlistProxy> {

    public WatchlistService(WatchlistProxy proxy) {

        super(proxy);
    }

    public List<Watchlist> reset() {

        this.getProxy().getRepository().deleteAll();
        return AnimeStatus.getDisplayable().stream()
                          .sorted(Comparator.comparingInt(Enum::ordinal))
                          .map(this::upsert)
                          .toList();
    }

    public Watchlist upsert(AnimeStatus status) {

        return this.getProxy().upsertEntity(
                status,
                WatchlistCreatedEvent::new,
                watchlist -> watchlist.setOrder(status.ordinal())
        );
    }

}
