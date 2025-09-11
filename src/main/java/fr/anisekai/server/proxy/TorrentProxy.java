package fr.anisekai.server.proxy;

import com.github.f4b6a3.uuid.UuidCreator;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.adapters.TorrentEventAdapter;
import fr.anisekai.server.events.TorrentCreatedEvent;
import fr.anisekai.server.exceptions.torrent.TorrentNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.TorrentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TorrentProxy extends ProxyService<Torrent, UUID, TorrentEventAdapter, TorrentRepository> {

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

        return this.create(entity -> {
            entity.setId(UuidCreator.getTimeOrderedEpoch());
            consumer.accept(entity);
        }, TorrentCreatedEvent::new);
    }

}
