package me.anisekai.library;

import me.anisekai.api.mkv.MediaTrack;
import me.anisekai.library.configuration.DiskConfiguration;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Media;
import me.anisekai.server.entities.Track;
import me.anisekai.server.interfaces.ITorrent;
import me.anisekai.server.services.MediaService;
import me.anisekai.server.services.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryService.class);

    private final DiskConfiguration configuration;
    private final MediaService      mediaService;
    private final TrackService      trackService;

    public LibraryService(DiskConfiguration configuration, MediaService mediaService, TrackService trackService) {

        this.configuration = configuration;

        this.mediaService = mediaService;
        this.trackService = trackService;
    }

    /**
     * Retrieve the {@link File} on disk for the provided {@link ITorrent}.
     *
     * @param torrent
     *         The {@link ITorrent} to retrieve.
     *
     * @return A {@link File}.
     */
    public File getTorrentFile(ITorrent<Episode> torrent) {

        return new File(this.configuration.getTorrentFile(), torrent.getFileName());
    }

    /**
     * Retrieve all {@link Track} associated to the provided {@link Episode}.
     *
     * @param episode
     *         The {@link Episode} to use when retrieving the {@link Track}
     *
     * @return A {@link List} of {@link Track}.
     */
    public List<Track> getTracks(Episode episode) {

        Media media = this.mediaService.find(episode);
        return this.trackService.getTracks(media);
    }

    public Media createMedia(Episode episode) {

        return this.mediaService.upsert(episode);
    }

    public List<Track> createTrack(Media media, Collection<MediaTrack> mediaTracks) {

        File        mediaDirectory = this.configuration.getMediaFile();
        List<Track> tracks         = this.trackService.getTracks(media);

        if (!tracks.isEmpty()) {
            // Delete all tracks, in case the task as been restarted.
            for (Track track : tracks) {
                File trackFile = track.asFile(mediaDirectory);
                if (trackFile.exists() && trackFile.isFile()) {
                    if (!trackFile.delete()) {
                        Path mediaOutput = mediaDirectory.toPath();
                        Path trackPath   = track.asFile(mediaDirectory).toPath();
                        Path relativized = mediaOutput.relativize(trackPath);
                        LOGGER.warn("Could not delete track file {}. This could cause issues later.", relativized);
                    }
                }
            }

            this.trackService.getProxy().getRepository().deleteAll(tracks);
        }

        return mediaTracks.stream()
                          .map(mediaTrack -> this.trackService.createFromMediaTrack(media, mediaTrack))
                          .collect(Collectors.toList());
    }

}
