package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Media;
import me.anisekai.server.events.MediaCreatedEvent;
import me.anisekai.server.interfaces.IMedia;
import me.anisekai.server.proxy.MediaProxy;
import me.anisekai.server.repositories.MediaRepository;
import org.springframework.stereotype.Service;

@Service
public class MediaService extends DataService<Media, Long, IMedia<Episode>, MediaRepository, MediaProxy> {

    public MediaService(MediaProxy proxy) {

        super(proxy);
    }

    public Media find(Episode episode) {

        return this.fetch(repository -> repository.findByEpisode(episode));
    }

    public Media upsert(Episode episode) {

        return this.getProxy().upsertEntity(
                repository -> repository.findByEpisode(episode),
                MediaCreatedEvent::new,
                entity -> {
                    entity.setEpisode(episode);
                    entity.setName(String.format("Ep%s", episode.getNumber()));
                }
        );
    }

}
