package fr.anisekai.library;

import fr.anisekai.sanctum.Sanctum;
import fr.anisekai.sanctum.enums.StorePolicy;
import fr.anisekai.sanctum.interfaces.FileStore;
import fr.anisekai.sanctum.interfaces.resolvers.StorageResolver;
import fr.anisekai.sanctum.stores.RawStorage;
import fr.anisekai.sanctum.stores.ScopedDirectoryStorage;
import fr.anisekai.sanctum.stores.ScopedFileStorage;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.TorrentFile;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class Library extends Sanctum {

    public static final FileStore CHUNKS       = new ScopedDirectoryStorage("chunks", Episode.class);
    public static final FileStore DOWNLOADS    = new RawStorage("downloads");
    public static final FileStore EVENT_IMAGES = new ScopedFileStorage("event-images", Anime.class, "webp");
    public static final FileStore SUBTITLES    = new ScopedDirectoryStorage("subs", Episode.class);

    public Library(@Value("${disk.media}") String location) {

        super(Path.of(location));

        this.registerStore(CHUNKS, StorePolicy.FULL_SWAP);
        this.registerStore(DOWNLOADS, StorePolicy.PRIVATE);
        this.registerStore(EVENT_IMAGES, StorePolicy.OVERWRITE);
        this.registerStore(SUBTITLES, StorePolicy.FULL_SWAP);
    }

    public Optional<Path> findDownload(TorrentFile torrentFile) {

        StorageResolver resolver = this.getResolver(DOWNLOADS);

        Path direct = resolver.file(torrentFile.getName());
        if (Files.isRegularFile(direct)) return Optional.of(direct);

        Torrent torrent   = torrentFile.getTorrent();
        Path    directory = resolver.directory(torrent.getName());
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
