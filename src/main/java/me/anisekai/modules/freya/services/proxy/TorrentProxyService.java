package me.anisekai.modules.freya.services.proxy;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.freya.entities.Torrent;
import me.anisekai.modules.freya.events.torrent.TorrentCreatedEvent;
import me.anisekai.modules.freya.exceptions.torrent.TorrentNotFoundException;
import me.anisekai.modules.freya.interfaces.ITorrent;
import me.anisekai.modules.freya.repositories.TorrentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class TorrentProxyService extends ProxyService<Torrent, Integer, ITorrent, TorrentRepository> {

    public TorrentProxyService(ApplicationEventPublisher publisher, TorrentRepository repository) {

        super(publisher, repository, Torrent::new);
    }

    @Override
    public Torrent getEntity(Function<TorrentRepository, Optional<Torrent>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(TorrentNotFoundException::new);
    }

    public UpsertResult<Torrent> upsert(int id, Consumer<ITorrent> consumer) {

        return this.upsert(repository -> repository.findById(id), consumer, TorrentCreatedEvent::new);
    }

}
