package me.anisekai.toshiko.controller;

import me.anisekai.toshiko.data.AnimeDirectory;
import me.anisekai.toshiko.data.EpisodeFile;
import me.anisekai.toshiko.data.GroupDirectory;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.helpers.RPC;
import me.anisekai.toshiko.helpers.containers.CachedField;
import me.anisekai.toshiko.repositories.AnimeNightRepository;
import me.anisekai.toshiko.repositories.AnimeRepository;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/raspberry")
public class RaspberryController {

    private static final List<ScheduledEvent.Status> STATUSES = Arrays.asList(
            ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED
    );

    private static final Logger LOGGER = LoggerFactory.getLogger(RaspberryController.class);

    private final CachedField<List<Map<String, Object>>> torrentCache = new CachedField<>();

    private final RPC                  rpc;
    private final AnimeRepository      animeRepository;
    private final AnimeNightRepository animeNightRepository;

    @Value("${toshiko.fs.root:#{null}}")
    private String fsRoot;

    public RaspberryController(RPC rpc, AnimeRepository animeRepository, AnimeNightRepository animeNightRepository) {

        this.rpc                  = rpc;
        this.animeRepository      = animeRepository;
        this.animeNightRepository = animeNightRepository;
    }

    @GetMapping(value = "/statuses", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> retrieveStatuses() {

        return Stream.of(AnimeStatus.values()).map(AnimeStatus::asMap).toList();
    }

    @GetMapping(value = "/animes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Anime> retrieveAnimes() {

        return this.animeRepository.findAll();
    }

    @GetMapping(value = "/anime-nights", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnimeNight> retrieveAnimeNights() {

        return this.animeNightRepository.findAllByStatusIn(STATUSES);
    }

    @GetMapping(value = "/torrents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getData() throws Exception {

        if (!this.rpc.isReady()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY);
        }

        if (this.torrentCache.isCacheValid()) {
            return this.torrentCache.get();
        }

        JSONObject                data           = this.rpc.getTorrents();
        JSONArray                 rawTorrentData = data.getJSONObject("arguments").getJSONArray("torrents");
        List<Map<String, Object>> torrents       = new ArrayList<>();
        for (int i = 0 ; i < rawTorrentData.length() ; i++) {
            torrents.add(rawTorrentData.getJSONObject(i).toMap());
        }

        this.torrentCache.set(torrents);
        return torrents;
    }

    @GetMapping(value = "/anidb", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnimeDirectory> getAnimeDatabase() {

        List<AnimeDirectory> directories = new ArrayList<>();

        if (this.fsRoot == null) {
            LOGGER.warn("FS ROOT not defined. Returning empty array.");
            return directories;
        }

        File fsRootDir = new File(this.fsRoot);

        if (!fsRootDir.exists()) {
            LOGGER.warn("FS ROOT not found. Returning empty array. (FS ROOT = {})", this.fsRoot);
            return directories;
        }

        for (File anime : Objects.requireNonNull(fsRootDir.listFiles())) {
            if (anime.isDirectory()) {
                directories.add(new AnimeDirectory(anime));
            }
        }

        directories.sort(Comparator.comparing(AnimeDirectory::getDisplayName));
        directories.forEach(anime -> {
            anime.getEpisodes().sort(Comparator.comparing(EpisodeFile::getDisplayName));
            anime.getGroups().sort(Comparator.comparing(GroupDirectory::getDisplayName));
            anime.getGroups().forEach(group -> group.getEpisodes().sort(Comparator.comparing(EpisodeFile::getDisplayName)));
        });

        return directories;
    }
}
