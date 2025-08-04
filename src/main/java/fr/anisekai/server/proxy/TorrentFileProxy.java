package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.TorrentFile;
import fr.anisekai.server.entities.adapters.TorrentFileEventAdapter;
import fr.anisekai.server.events.TorrentFileCreatedEvent;
import fr.anisekai.server.exceptions.torrent.TorrentNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.TorrentFileRepository;
import fr.anisekai.wireless.remote.keys.TorrentKey;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TorrentFileProxy extends ProxyService<TorrentFile, TorrentKey, TorrentFileEventAdapter, TorrentFileRepository> {

    public TorrentFileProxy(ApplicationEventPublisher publisher, TorrentFileRepository repository) {

        super(publisher, repository, TorrentFile::new);
    }

    @Override
    public TorrentFile getEntity(Function<TorrentFileRepository, Optional<TorrentFile>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(TorrentNotFoundException::new);
    }

    public TorrentFile create(Consumer<TorrentFileEventAdapter> consumer) {

        return this.create(consumer, TorrentFileCreatedEvent::new);
    }

}
