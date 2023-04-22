package me.anisekai.toshiko.io;

import jakarta.annotation.PostConstruct;
import me.anisekai.toshiko.io.entities.DiskAnime;
import me.anisekai.toshiko.io.entities.DiskEpisode;
import me.anisekai.toshiko.io.entities.DiskGroup;
import me.anisekai.toshiko.io.video.SubtitleCodec;
import me.anisekai.toshiko.utils.FileSystemUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${toshiko.fs.automation}")
    private String fsAutomation;

    @Value("${toshiko.fs.animes}")
    private String fsAnimes;

    @Value("${toshiko.fs.subtitles}")
    private String fsSubtitles;

    private Path automationPath;
    private Path animePath;
    private Path subtitlePath;

    private Set<DiskAnime> database;
    private JSONArray      databaseCache;

    public DiskService() {

        this.database      = new TreeSet<>(Comparator.comparing(DiskAnime::getName));
        this.databaseCache = new JSONArray();
    }

    @PostConstruct
    private void initialize() {

        this.automationPath = Path.of(this.fsAutomation);
        this.animePath      = Path.of(this.fsAnimes);
        this.subtitlePath   = Path.of(this.fsSubtitles);

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

    public String getFsAutomation() {

        return this.fsAutomation;
    }

    public String getFsAnimes() {

        return this.fsAnimes;
    }

    public String getFsSubtitles() {

        return this.fsSubtitles;
    }

    public Set<DiskAnime> getDatabase() {

        return this.database;
    }

    public JSONArray getDatabaseCache() {

        return this.databaseCache;
    }

    public void cache() {
        // Build the cache.

        Set<DiskAnime> cache = new TreeSet<>(Comparator.comparing(DiskAnime::getName));

        for (File anime : FileSystemUtils.files(this.fsAnimes)) {

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
        cache.forEach(a -> a.finalize(this.fsAnimes, this.fsSubtitles));
        LOGGER.debug("Finalized.");

        // Apply cache
        this.database = cache;

        LOGGER.info("Jsonify...");
        this.databaseCache.clear();
        this.database.stream().map(DiskAnime::toJson).forEach(this.databaseCache::put);
        LOGGER.info("OK. Cached {} items", this.database.size());
    }
}
