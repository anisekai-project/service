package me.anisekai.toshiko.services;

import me.anisekai.toshiko.helpers.fs.AnimeFs;
import me.anisekai.toshiko.helpers.fs.EpisodeFs;
import me.anisekai.toshiko.helpers.fs.GroupFs;
import me.anisekai.toshiko.utils.FileSystemUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class StorageService {

    public static final String HTTP_ANIME_ROOT = "https://toshiko.alexpado.fr/animes";
    public static final String HTTP_SUBS_ROOT  = "https://toshiko.alexpado.fr/subtitles";

    private static final Logger       LOGGER         = LoggerFactory.getLogger(StorageService.class);
    private static       List<String> SUBTITLES_EXTS = Arrays.asList(".srt", ".ass", ".ssa", ".vtt");

    @Value("${toshiko.fs.animes}")
    private String fileSystemAnimePath;

    @Value("${toshiko.fs.subtitles}")
    private String fileSystemSubtitlePath;

    private Set<AnimeFs> database;
    private JSONArray    databaseCache;

    public StorageService() {

        this.database      = new TreeSet<>(Comparator.comparing(AnimeFs::getName));
        this.databaseCache = new JSONArray();
    }

    public Set<AnimeFs> getDatabase() {

        return this.database;
    }

    public JSONArray getDatabaseCache() {

        return this.databaseCache;
    }

    private boolean shouldIgnoreFile(File file) {

        return SUBTITLES_EXTS.stream().anyMatch(file.getName()::endsWith);
    }

    public void cache() {
        // Build the cache.

        Set<AnimeFs> cache = new TreeSet<>(Comparator.comparing(AnimeFs::getName));

        for (File anime : FileSystemUtils.files(this.fileSystemAnimePath)) {

            if (anime.isFile()) {
                continue;
            }

            LOGGER.debug("Scanning folder {}", anime.getName());
            AnimeFs animeFs = new AnimeFs(anime);

            for (File animeContent : FileSystemUtils.files(anime)) {

                if (animeContent.isFile()) {
                    if (this.shouldIgnoreFile(animeContent)) {
                        LOGGER.debug("  Ignoring file {}", animeContent.getName());
                        continue;
                    }

                    LOGGER.debug("  Handling file {}", animeContent.getName());
                    EpisodeFs file = new EpisodeFs(animeContent);
                    animeFs.add(file);

                    continue;
                }

                LOGGER.debug("  Scanning folder {}", anime.getName());
                GroupFs groupFs = new GroupFs(animeContent);
                animeFs.add(groupFs);

                for (File groupContent : FileSystemUtils.files(animeContent)) {
                    if (groupContent.isFile()) {
                        if (this.shouldIgnoreFile(groupContent)) {
                            LOGGER.debug("    Ignoring file {}", groupContent.getName());
                            continue;
                        }

                        LOGGER.debug("    Handling file {}", groupContent.getName());
                        EpisodeFs file = new EpisodeFs(groupContent);
                        groupFs.add(file);
                        continue;
                    }

                    LOGGER.debug("    Ignoring folder {}", groupContent.getName());
                }
            }

            cache.add(animeFs);
        }

        LOGGER.debug("Finalizing...");
        cache.forEach(a -> a.finalize(this.fileSystemAnimePath, this.fileSystemSubtitlePath));
        LOGGER.debug("Finalized.");

        // Apply cache
        this.database = cache;

        LOGGER.info("Jsonify...");
        this.databaseCache.clear();
        this.database.stream().map(AnimeFs::toJson).forEach(this.databaseCache::put);
        LOGGER.info("OK. Cached {} items", this.database.size());
    }

}
