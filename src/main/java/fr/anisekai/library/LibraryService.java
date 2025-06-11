package fr.anisekai.library;

import fr.anisekai.wireless.remote.interfaces.TorrentEntity;
import fr.anisekai.wireless.remote.interfaces.TorrentFileEntity;
import fr.anisekai.library.configuration.DiskConfiguration;
import fr.anisekai.server.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class LibraryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryService.class);

    private final DiskConfiguration configuration;

    public LibraryService(DiskConfiguration configuration) {

        this.configuration = configuration;
    }

    public File get(TorrentEntity torrent) {

        File download = this.configuration.getDownloadFile();
        return new File(download, torrent.getName());
    }

    public File get(TorrentFileEntity<Episode, Torrent> torrentFile) {

        File download = this.configuration.getDownloadFile();
        return new File(download, torrentFile.getName());
    }

    public File get(TorrentEntity torrent, TorrentFileEntity<Episode, Torrent> torrentFile) {

        File torrentDir = this.get(torrent);
        return new File(torrentDir, torrentFile.getName());
    }

    public File get(Track track) {

        // <root>/<anime_id>/<episode_id>/<track_name>.<ext>

        Episode episode = track.getEpisode();
        Anime   anime   = episode.getAnime();

        File animeTarget   = new File(this.configuration.getMediaFile(), anime.getId().toString());
        File episodeTarget = new File(animeTarget, episode.getId().toString());
        return new File(episodeTarget, track.getName() + "." + track.getCodec().getExtension());
    }

}
