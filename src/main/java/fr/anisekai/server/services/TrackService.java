package fr.anisekai.server.services;

import fr.anisekai.wireless.api.media.MediaFile;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import fr.anisekai.server.entities.adapters.TrackEventAdapter;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.TrackProxy;
import fr.anisekai.server.repositories.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService extends DataService<Track, Long, TrackEventAdapter, TrackRepository, TrackProxy> {

    public TrackService(TrackProxy proxy) {

        super(proxy);
    }

    public List<Track> getTracks(Episode episode) {

        return this.fetchAll(repository -> repository.findByEpisode(episode));
    }

    public List<Track> createFromMediaTrack(Episode episode, MediaFile mediaFile) {

        return mediaFile.getStreams().stream()
                        .map(stream -> this.getProxy().create(track -> {
                            track.setEpisode(episode);
                            track.setName("Track " + stream.getId());
                            track.setCodec(stream.getCodec());
                            track.setLanguage(stream.getMetadata().get("language"));
                        })).toList();
    }

}
