package fr.anisekai.library;

import fr.anisekai.library.configuration.DiskConfiguration;
import fr.anisekai.library.enums.LibraryStore;
import fr.anisekai.library.exceptions.IncompatibleStoreException;
import fr.anisekai.library.exceptions.IncompatibleTrackException;
import fr.anisekai.library.exceptions.StoreBreakOutException;
import fr.anisekai.library.exceptions.StoreUnavailableException;
import fr.anisekai.utils.FileUrlStreamer;
import fr.anisekai.wireless.api.media.MediaFile;
import fr.anisekai.wireless.api.media.bin.FFMpeg;
import fr.anisekai.wireless.api.media.enums.CodecType;
import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.remote.interfaces.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Random;

@Service
public class LibraryService {

    private final DiskConfiguration configuration;

    public LibraryService(DiskConfiguration configuration) {

        this.configuration = configuration;
    }

    /**
     * Retrieve the {@link File} pointing toward the provided {@link LibraryStore} directory. If the target directory
     * does not exist, it will be created.
     *
     * @param store
     *         The {@link LibraryStore}.
     *
     * @return A {@link File} pointing toward the directory of {@link LibraryStore}.
     */
    private File getStoreOf(LibraryStore store) {

        if (store.isEntityStore()) {
            throw new IncompatibleStoreException(String.format(
                    "The store '%s' cannot be used without an entity.",
                    store.getStoreName()
            ));
        }
        File target = new File(this.configuration.getMedia(), store.getStoreName());
        if (!target.exists()) {
            if (!target.mkdir()) {
                throw new StoreUnavailableException(String.format(
                        "Could not create '%s' store folder.",
                        store.getStoreName()
                ));
            }
        }
        return target;
    }

    /**
     * Retrieve the {@link File} pointing toward the provided entity {@link LibraryStore} directory. If the target
     * directory does not exist, it will be created.
     *
     * @param store
     *         The {@link LibraryStore}.
     *
     * @return A {@link File} pointing toward the directory of {@link LibraryStore}.
     */
    public File getStoreOf(LibraryStore store, Entity<?> entity) {

        if (!store.isEntityStore()) {
            throw new IncompatibleStoreException(String.format(
                    "The store '%s' cannot be used with an entity.",
                    store.getStoreName()
            ));
        }

        File storeDir = new File(this.configuration.getMedia(), store.getStoreName());
        File target   = new File(storeDir, entity.getId().toString());
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new StoreUnavailableException(String.format(
                        "Could not create '%s/%s' entity store folder.",
                        store.getStoreName(),
                        entity.getId().toString()
                ));
            }
        }
        return target;
    }

    /**
     * Check if the provided {@link File} is within the expected {@link LibraryStore}. This check avoid any 'break-out'
     * of the library using relative paths.
     *
     * @param store
     *         The {@link LibraryStore} into which the file should be located.
     * @param file
     *         The {@link File} to check for.
     *
     * @return True if the {@link File} is within the expected folder, false otherwise.
     */
    public boolean isWithinStore(LibraryStore store, File file) {

        Path storePath = this.getStoreOf(store).toPath().toAbsolutePath().normalize();
        Path filePath  = file.toPath().toAbsolutePath().normalize();

        return filePath.startsWith(storePath);
    }

    /**
     * Check if the provided {@link File} is within the expected {@link LibraryStore}. This check avoid any 'break-out'
     * of the library using relative paths.
     *
     * @param store
     *         The {@link LibraryStore} into which the file should be located.
     * @param entity
     *         The {@link Entity} for which the file should be located.
     * @param file
     *         The {@link File} to check for.
     *
     * @return True if the {@link File} is within the expected folder, false otherwise.
     */
    public boolean isWithinStore(LibraryStore store, Entity<?> entity, File file) {

        Path storePath = this.getStoreOf(store, entity).toPath().toAbsolutePath().normalize();
        Path filePath  = file.toPath().toAbsolutePath().normalize();

        return filePath.startsWith(storePath);
    }

    /**
     * Request a temporary file with the provided extension. The returned file will not be automatically created, and
     * will never exist.
     * <p>
     * <b>Note:</b> Although one might think the file is being deleted after use, this is not the case as there is no
     * reliable way to determine that the file is still being used or not. It is the responsibility of the calling code
     * to remove it after being used. This method merely allow anyone to obtain a file to work with that will not
     * interfere with any other files.
     *
     * @param fileExtension
     *         The file extension to use for the temporary file.
     *
     * @return A {@link File} that can be used.
     */
    public File requestTemporaryFile(String fileExtension) {

        File store = this.getStoreOf(LibraryStore.TEMPORARY);
        long time  = System.currentTimeMillis();
        int  rnd   = new Random().nextInt(100, 999);

        File tmp = new File(store, "%s.%s.%s".formatted(time, rnd, fileExtension));

        if (!this.isWithinStore(LibraryStore.TEMPORARY, tmp)) {
            throw new IllegalArgumentException("The requested temporary file is out of bound.");
        }

        if (tmp.exists()) {
            // In worst case scenario, it will loop until the next millisecond.
            return this.requestTemporaryFile(fileExtension);
        }

        return tmp;
    }

    /**
     * Download the image and store it as the event image for the provided {@link AnimeEntity}.
     *
     * @param anime
     *         The {@link AnimeEntity} for which the image should be stored.
     * @param imageUrl
     *         The url pointing to image to download.
     */
    public void storeAnimeEventImage(AnimeEntity<?> anime, String imageUrl) throws Exception {

        File store  = this.getStoreOf(LibraryStore.EVENT_IMAGES);
        File target = new File(store, "%s.png".formatted(anime.getId()));

        if (!new FileUrlStreamer(target, imageUrl).complete()) {
            throw new IllegalStateException("The file was not downloaded.");
        }
    }

    /**
     * Retrieve the event image for the provided {@link AnimeEntity}.
     *
     * @param anime
     *         The {@link AnimeEntity} for which the image should be retrieved.
     */
    public File retrieveAnimeEventImage(AnimeEntity<?> anime) {

        File store = this.getStoreOf(LibraryStore.EVENT_IMAGES);
        return new File(store, "%s.png".formatted(anime.getId()));
    }

    /**
     * Retrieve a chunk {@link File} from the library for the provided {@link EpisodeEntity}.
     *
     * @param episode
     *         The {@link EpisodeEntity} for which the file will be retrieved.
     * @param chunkName
     *         The name of the chunk.
     */
    public File retrieveEpisodeChunk(EpisodeEntity<?> episode, String chunkName) {

        File store  = this.getStoreOf(LibraryStore.CHUNKS, episode);
        File target = new File(store, chunkName);

        if (!this.isWithinStore(LibraryStore.CHUNKS, target)) {
            throw new StoreBreakOutException("The requested chunk is out of bound.", target);
        }

        return target;
    }

    /**
     * Store the provided {@link File} into the library for the provided {@link EpisodeEntity}. This method will call
     * ffmpeg to convert the targeted file into the right format for the application without removing the original.
     *
     * @param episode
     *         The {@link EpisodeEntity} for which the file will be imported.
     * @param file
     *         The {@link File} to import.
     */
    public void storeEpisode(EpisodeEntity<?> episode, File file) throws IOException, InterruptedException {

        File      store = this.getStoreOf(LibraryStore.CHUNKS, episode);
        MediaFile media = MediaFile.of(file);

        FFMpeg.createMpd(media, store);
    }

    /**
     * Store the {@link File} from the library for the provided {@link EpisodeEntity}.
     *
     * @param track
     *         The {@link TrackEntity} for which the file will be retrieved.
     */
    public File retrieveSubtitle(TrackEntity<?> track) {

        if (track.getCodec().getType() != CodecType.SUBTITLE) {
            throw new IncompatibleTrackException(
                    "Cannot retrieve a non-subtitle track from the subtitle store.",
                    track
            );
        }

        File   store    = this.getStoreOf(LibraryStore.SUBTITLE, track.getEpisode());
        String filename = "%s.%s".formatted(track.getId(), track.getCodec().getExtension());
        return new File(store, filename);
    }

    /**
     * Store the provided {@link File} into the library for the provided {@link EpisodeEntity}. This method will copy
     * the provided file into the library without removing the original.
     *
     * @param track
     *         The {@link TrackEntity} for which the file will be imported.
     * @param file
     *         The {@link File} to import.
     */
    public void storeSubtitle(TrackEntity<?> track, File file) throws IOException {

        if (track.getCodec().getType() != CodecType.SUBTITLE) {
            throw new IncompatibleTrackException("Cannot store a non-subtitle track into the subtitle store.", track);
        }

        File target = this.retrieveSubtitle(track);
        Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Retrieve the {@link File} corresponding to the {@link TorrentFileEntity}. This method will go on a best-effort
     * basis to determine if the file is within the main {@link TorrentEntity} folder or directly into the
     * {@link LibraryStore#DOWNLOAD} folder.
     *
     * @param torrentFile
     *         The {@link TorrentFileEntity} from which the file should be retrieved.
     *
     * @return The {@link File} pointing to the {@link TorrentEntity}.
     */
    public Optional<File> retrieveDownload(TorrentFileEntity<?, ?> torrentFile) throws IOException {

        File store  = this.getStoreOf(LibraryStore.DOWNLOAD);
        File direct = new File(store, torrentFile.getName());
        if (direct.exists()) return Optional.of(direct);
        File torrentStore = new File(store, torrentFile.getTorrent().getName());
        File subTarget    = new File(torrentStore, torrentFile.getName());
        if (subTarget.exists()) return Optional.of(subTarget);
        return Optional.empty();
    }

}
