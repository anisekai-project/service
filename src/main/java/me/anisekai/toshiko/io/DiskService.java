package me.anisekai.toshiko.io;

import jakarta.annotation.PostConstruct;
import me.anisekai.toshiko.configurations.ToshikoDiskConfiguration;
import me.anisekai.toshiko.configurations.ToshikoFeatureConfiguration;
import me.anisekai.toshiko.io.entities.DiskAnime;
import me.anisekai.toshiko.io.entities.DiskEpisode;
import me.anisekai.toshiko.io.entities.DiskGroup;
import me.anisekai.toshiko.io.video.SubtitleCodec;
import me.anisekai.toshiko.utils.FileSystemUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Service
public class DiskService {

    public static final String HTTP_ANIME_ROOT = "https://toshiko.alexpado.fr/animes";
    public static final String HTTP_SUBS_ROOT  = "https://toshiko.alexpado.fr/subtitles";

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskService.class);

    private final ToshikoFeatureConfiguration featureConfiguration;
    private final ToshikoDiskConfiguration    diskConfiguration;

    private Path automationPath;
    private Path animePath;
    private Path subtitlePath;
    private Path torrentPath;

    private       Set<DiskAnime> database;
    private final JSONArray      databaseCache;

    public DiskService(ToshikoFeatureConfiguration featureConfiguration, ToshikoDiskConfiguration diskConfiguration) {

        this.featureConfiguration = featureConfiguration;
        this.diskConfiguration    = diskConfiguration;
        this.database             = new TreeSet<>(Comparator.comparing(DiskAnime::getName));
        this.databaseCache        = new JSONArray();
    }

    @PostConstruct
    private void initialize() {

        this.automationPath = Path.of(this.diskConfiguration.getAnimesInput());
        this.animePath      = Path.of(this.diskConfiguration.getAnimesOutput());
        this.subtitlePath   = Path.of(this.diskConfiguration.getSubtitlesOutput());
        this.torrentPath    = Path.of(this.diskConfiguration.getTorrentInput());

        this.cache();
    }

    public Path getAutomationPath() {

        return this.automationPath;
    }

    public Path getAnimePath() {

        return this.animePath;
    }

    public Path getSubtitlePath() {

        return this.subtitlePath;
    }

    public Path getTorrentPath() {

        return this.torrentPath;
    }

    public ToshikoDiskConfiguration getDiskConfiguration() {

        return this.diskConfiguration;
    }

    public boolean isEnabled() {

        return this.featureConfiguration.isDiskEnabled();
    }

    public Set<DiskAnime> getDatabase() {

        return this.database;
    }

    public JSONArray getDatabaseCache() {

        return this.databaseCache;
    }

    public void cache() {
        // Build the cache.
        if (!this.featureConfiguration.isDiskEnabled()) {
            return;
        }

        Set<DiskAnime> cache = new TreeSet<>(Comparator.comparing(DiskAnime::getName));

        for (File anime : FileSystemUtils.files(this.diskConfiguration.getAnimesOutput())) {

            if (anime.isFile()) {
                continue;
            }

            LOGGER.debug("Scanning folder {}", anime.getName());
            DiskAnime diskAnime = new DiskAnime(anime);

            for (File animeContent : FileSystemUtils.files(anime)) {

                if (animeContent.isFile()) {
                    if (SubtitleCodec.isSubtitle(animeContent)) {
                        LOGGER.debug("  Ignoring file {}", animeContent.getName());
                        continue;
                    }

                    LOGGER.debug("  Handling file {}", animeContent.getName());
                    DiskEpisode file = new DiskEpisode(animeContent);
                    diskAnime.add(file);

                    continue;
                }

                LOGGER.debug("  Scanning folder {}", anime.getName());
                DiskGroup diskGroup = new DiskGroup(animeContent);
                diskAnime.add(diskGroup);

                for (File groupContent : FileSystemUtils.files(animeContent)) {
                    if (groupContent.isFile()) {
                        if (SubtitleCodec.isSubtitle(groupContent)) {
                            LOGGER.debug("    Ignoring file {}", groupContent.getName());
                            continue;
                        }

                        LOGGER.debug("    Handling file {}", groupContent.getName());
                        DiskEpisode file = new DiskEpisode(groupContent);
                        diskGroup.add(file);
                        continue;
                    }

                    LOGGER.debug("    Ignoring folder {}", groupContent.getName());
                }
            }

            cache.add(diskAnime);
        }

        LOGGER.debug("Finalizing...");
        cache.forEach(a -> a.finalize(this.diskConfiguration.getAnimesOutput(), this.diskConfiguration.getSubtitlesOutput()));
        LOGGER.debug("Finalized.");

        // Apply cache
        this.database = cache;

        LOGGER.info("Jsonify...");
        this.databaseCache.clear();
        this.database.stream().map(DiskAnime::toJson).forEach(this.databaseCache::put);
        LOGGER.info("OK. Cached {} items", this.database.size());
    }

}
