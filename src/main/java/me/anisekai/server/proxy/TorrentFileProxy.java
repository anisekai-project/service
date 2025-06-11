package me.anisekai.server.proxy;

import fr.anisekai.wireless.remote.keys.TorrentKey;
import me.anisekai.server.entities.TorrentFile;
import me.anisekai.server.entities.adapters.TorrentFileEventAdapter;
import me.anisekai.server.events.TorrentFileCreatedEvent;
import me.anisekai.server.exceptions.torrent.TorrentNotFoundException;
import me.anisekai.server.persistence.ProxyService;
import me.anisekai.server.repositories.TorrentFileRepository;
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
