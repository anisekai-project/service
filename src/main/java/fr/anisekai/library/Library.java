package fr.anisekai.library;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.TorrentFile;
import fr.anisekai.wireless.api.storage.LibraryManager;
import fr.anisekai.wireless.api.storage.containers.stores.EntityDirectoryStore;
import fr.anisekai.wireless.api.storage.containers.stores.EntityFileStore;
import fr.anisekai.wireless.api.storage.containers.stores.RawStorageStore;
import fr.anisekai.wireless.api.storage.enums.StorePolicy;
import fr.anisekai.wireless.api.storage.interfaces.StorageStore;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class Library extends LibraryManager {

    public static final StorageStore CHUNKS       = new EntityDirectoryStore("chunks", Episode.class);
    public static final StorageStore DOWNLOADS    = new RawStorageStore("downloads");
    public static final StorageStore EVENT_IMAGES = new EntityFileStore("event-images", Anime.class, "png");
    public static final StorageStore SUBTITLES    = new EntityDirectoryStore("subs", Episode.class);

    public Library(@Value("${disk.media}") String location) {

        super(Path.of(location));

        this.registerStore(CHUNKS, StorePolicy.FULL_SWAP);
        this.registerStore(DOWNLOADS, StorePolicy.PRIVATE);
        this.registerStore(EVENT_IMAGES, StorePolicy.OVERWRITE);
        this.registerStore(SUBTITLES, StorePolicy.FULL_SWAP);
    }

    public Optional<Path> findDownload(TorrentFile torrentFile) {

        Path direct = this.resolveFile(DOWNLOADS, torrentFile.getName());
        if (Files.isRegularFile(direct)) return Optional.of(direct);

        Torrent torrent   = torrentFile.getTorrent();
        Path    store     = this.resolveDirectory(DOWNLOADS);
        Path    directory = store.resolve(torrent.getName());
        if (!Files.isDirectory(directory)) return Optional.empty();

        Path target = directory.resolve(torrentFile.getName());
        if (Files.isRegularFile(target)) return Optional.of(target);
        return Optional.empty();
    }

    @PreDestroy
    private void onClose() throws Exception {

        this.close();
    }

}
