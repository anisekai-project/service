package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.events.torrent.TorrentCreatedEvent;
import me.anisekai.toshiko.exceptions.torrent.TorrentNotFoundException;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.ITorrent;
import me.anisekai.toshiko.repositories.TorrentRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class TorrentProxyService extends AbstractProxyService<Torrent, Integer, ITorrent, TorrentRepository> {

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
