package fr.anisekai.library;

import fr.anisekai.library.configuration.DiskConfiguration;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.wireless.remote.interfaces.EpisodeEntity;
import fr.anisekai.wireless.remote.interfaces.TorrentEntity;
import fr.anisekai.wireless.remote.interfaces.TorrentFileEntity;
import fr.anisekai.wireless.remote.interfaces.TrackEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

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

    public File useTemporaryFile(String extension) {

        String filename = "%s.%s".formatted(System.currentTimeMillis(), extension);
        this.configuration.getMediaTemp().mkdirs();
        return new File(this.configuration.getMediaTemp(), filename);
    }

    public File getSubStore(EpisodeEntity<?> episode) {
        // library/subs/<ep>
        return new File(this.configuration.getMediaSubs(), episode.getId().toString());
    }

    public File getSubFile(TrackEntity<?> track) {
        // library/subs/<ep>/<id>.<ext>
        String filename = "%s.%s".formatted(track.getId(), track.getCodec().getExtension());
        return new File(this.getSubStore(track.getEpisode()), filename);
    }

    public File getChunksStore(EpisodeEntity<?> episode) {
        // library/chunks/<ep>
        File store = new File(this.configuration.getMediaChunks(), episode.getId().toString());
        store.mkdirs();
        return store;
    }

    public File getMetaFile(EpisodeEntity<?> episode) {
        // library/chunks/<ep>/meta.mpd
        return new File(this.getChunksStore(episode), "meta.mpd");
    }

    public boolean isWithinLibrary(File file) {

        Path mediaPath = this.configuration.getMediaFile().toPath().toAbsolutePath().normalize();
        Path filePath  = file.toPath().toAbsolutePath().normalize();

        return filePath.startsWith(mediaPath);
    }


}
