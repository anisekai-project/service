package me.anisekai.modules.freya.services;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.mkv.MediaTrackType;
import me.anisekai.modules.freya.configurations.FreyaConfiguration;
import me.anisekai.modules.freya.entities.detached.disk.DiskAnime;
import me.anisekai.modules.freya.entities.detached.disk.DiskEpisode;
import me.anisekai.modules.freya.entities.detached.disk.DiskGroup;
import me.anisekai.modules.freya.entities.detached.disk.DiskSubtitle;
import me.anisekai.modules.freya.utils.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Service
public class DiskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskService.class);

    private final FreyaConfiguration configuration;

    private Path contentPath;
    private Path automationPath;
    private Path torrentPath;

    private Set<DiskAnime> database;

    public DiskService(FreyaConfiguration configuration) {

        this.configuration = configuration;
        this.database      = new TreeSet<>(Comparator.comparing(DiskAnime::getName));
    }

    @PostConstruct
    private void initialize() {

        this.contentPath    = this.getContentDirectory().toPath();
        this.automationPath = this.getAutomationDirectory().toPath();
        this.torrentPath    = this.getTorrentsDirectory().toPath();

        this.cache();
    }

    public File getContentDirectory() {

        return new File(this.configuration.getLibrary(), "content");
    }

    public File getAutomationDirectory() {

        return new File(this.configuration.getLibrary(), "automation");
    }

    public File getTorrentsDirectory() {

        return new File(this.configuration.getLibrary(), "torrents");
    }

    public File getAnimesRoot() {

        return new File(this.getContentDirectory(), "animes");
    }

    public File getSubtitlesRoot() {

        return new File(this.getContentDirectory(), "subtitles");
    }

    public FreyaConfiguration getConfiguration() {

        return this.configuration;
    }

    public Set<DiskAnime> getDatabase() {

        return this.database;
    }

    public void cache() {

        // As everything will be in the database in the future, this method should be used only to check the disk
        // rather than caching its content.


        // Build the cache.
        if (!this.configuration.isLibraryScanEnabled()) {
            return;
        }

        Set<DiskAnime> cache = new TreeSet<>(Comparator.comparing(DiskAnime::getName));

        File content            = this.contentPath.toFile();
        File animeDirectory     = new File(content, "animes");
        File subtitlesDirectory = new File(content, "subtitles");


        for (File anime : FileSystemUtils.files(animeDirectory)) {

            if (anime.isFile()) {
                continue;
            }

            LOGGER.debug("Scanning folder {}", anime.getName());
            DiskAnime diskAnime = new DiskAnime(anime);

            for (File animeContent : FileSystemUtils.files(anime)) {

                if (animeContent.isFile()) {
                    if (MediaTrackType.SUBTITLES.is(animeContent)) {
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
                        if (MediaTrackType.SUBTITLES.is(groupContent)) {
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
        cache.forEach(a -> a.finalize(
                animeDirectory.getAbsolutePath(),
                subtitlesDirectory.getAbsolutePath()
        ));
        LOGGER.debug("Finalized.");

        // Apply cache
        this.database = cache;
        LOGGER.info("OK. Cached {} items", this.database.size());
    }


    public Optional<DiskEpisode> findEpisode(UUID anime, UUID episode) {

        return this.getDatabase()
                   .stream()
                   .filter(diskAnime -> diskAnime.getUuid().equals(anime))
                   .findFirst()
                   .flatMap(diskAnime -> diskAnime
                           .getEpisodes()
                           .stream()
                           .filter(diskEpisode -> diskEpisode.getUuid().equals(episode))
                           .findFirst()
                   );
    }

    public Optional<DiskEpisode> findEpisode(UUID anime, UUID group, UUID episode) {

        return this.getDatabase()
                   .stream()
                   .filter(diskAnime -> diskAnime.getUuid().equals(anime))
                   .findFirst()
                   .flatMap(diskAnime -> diskAnime
                           .getGroups()
                           .stream()
                           .filter(diskGroup -> diskGroup.getUuid().equals(group))
                           .findFirst()
                           .flatMap(diskGroup -> diskGroup
                                   .getEpisodes()
                                   .stream()
                                   .filter(diskEpisode -> diskEpisode.getUuid().equals(episode))
                                   .findFirst()
                           )
                   );
    }

    public Optional<DiskSubtitle> findSubtitle(DiskEpisode episode, UUID subTrack) {

        return episode.getSubtitles().stream()
                      .filter(diskSubtitle -> diskSubtitle.getUuid().equals(subTrack))
                      .findFirst();
    }

}
