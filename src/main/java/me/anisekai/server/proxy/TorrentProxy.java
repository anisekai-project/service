package me.anisekai.server.proxy;

import me.anisekai.server.entities.Torrent;
import me.anisekai.server.entities.adapters.TorrentEventAdapter;
import me.anisekai.server.events.TorrentCreatedEvent;
import me.anisekai.server.exceptions.torrent.TorrentNotFoundException;
import me.anisekai.server.persistence.ProxyService;
import me.anisekai.server.repositories.TorrentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TorrentProxy extends ProxyService<Torrent, String, TorrentEventAdapter, TorrentRepository> {

    public TorrentProxy(ApplicationEventPublisher publisher, TorrentRepository repository) {

        super(publisher, repository, Torrent::new);
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
    public Torrent getEntity(Function<TorrentRepository, Optional<Torrent>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(TorrentNotFoundException::new);
    }

    public Torrent create(Consumer<TorrentEventAdapter> consumer) {

        return this.create(consumer, TorrentCreatedEvent::new);
    }

}
