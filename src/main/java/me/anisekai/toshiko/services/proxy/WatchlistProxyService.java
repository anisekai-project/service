package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.exceptions.watchlist.WatchlistNotFoundException;
import me.anisekai.toshiko.interfaces.entities.IWatchlist;
import me.anisekai.toshiko.repositories.WatchlistRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class WatchlistProxyService extends AbstractProxyService<Watchlist, AnimeStatus, IWatchlist, WatchlistRepository> {

    public WatchlistProxyService(ApplicationEventPublisher publisher, WatchlistRepository repository) {

        super(publisher, repository, Watchlist::new);
    }

    @Override
    public Watchlist getEntity(Function<WatchlistRepository, Optional<Watchlist>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(WatchlistNotFoundException::new);
    }

}
