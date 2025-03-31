package me.anisekai.server.services;

import me.anisekai.api.mkv.MediaTrack;
import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.Media;
import me.anisekai.server.entities.Track;
import me.anisekai.server.enums.TrackType;
import me.anisekai.server.interfaces.ITrack;
import me.anisekai.server.proxy.TrackProxy;
import me.anisekai.server.repositories.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService extends DataService<Track, Long, ITrack<Media>, TrackRepository, TrackProxy> {

    public TrackService(TrackProxy proxy) {

        super(proxy);
    }

    public List<Track> getTracks(Media media) {

        return this.fetchAll(repository -> repository.findByMedia(media));
    }

    public Track createFromMediaTrack(Media media, MediaTrack mediaTrack) {

        return this.getProxy().create(entity -> {
            entity.setMedia(media);
            entity.setName(mediaTrack.getTrackName());
            entity.setType(TrackType.ofMediaTrackType(mediaTrack.getType()));
        });
    }

}
