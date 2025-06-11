package fr.anisekai.server.proxy;

import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.server.entities.Watchlist;
import fr.anisekai.server.entities.adapters.WatchlistEventAdapter;
import fr.anisekai.server.events.WatchlistCreatedEvent;
import fr.anisekai.server.exceptions.watchlist.WatchlistNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.WatchlistRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class WatchlistProxy extends ProxyService<Watchlist, AnimeList, WatchlistEventAdapter, WatchlistRepository> {

    public WatchlistProxy(ApplicationEventPublisher publisher, WatchlistRepository repository) {

        super(publisher, repository, Watchlist::new);
    }

    /**
     * Same as {@link #fetchEntity(Function)} but should ensure that the selector should not return any empty optional
     * instance by throwing any {@link RuntimeException} using {@link Optional#orElseThrow(Supplier)}.
     *
     * @param selector
     *         The selector to use to retrieve the entity.
     *
     * @return The entity instance.
     */
    @Override
    public Watchlist getEntity(Function<WatchlistRepository, Optional<Watchlist>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(WatchlistNotFoundException::new);
    }

    public Watchlist create(Consumer<WatchlistEventAdapter> consumer) {

        return this.create(consumer, WatchlistCreatedEvent::new);
    }

}
