package me.anisekai.modules.shizue.services.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.entities.Watchlist;
import me.anisekai.modules.shizue.exceptions.watchlist.WatchlistNotFoundException;
import me.anisekai.modules.shizue.interfaces.entities.IWatchlist;
import me.anisekai.modules.shizue.repositories.WatchlistRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class WatchlistProxyService extends ProxyService<Watchlist, AnimeStatus, IWatchlist, WatchlistRepository> {

    public WatchlistProxyService(ApplicationEventPublisher publisher, WatchlistRepository repository) {

        super(publisher, repository, Watchlist::new);
    }

    @Override
    public Watchlist getEntity(Function<WatchlistRepository, Optional<Watchlist>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(WatchlistNotFoundException::new);
    }

}
