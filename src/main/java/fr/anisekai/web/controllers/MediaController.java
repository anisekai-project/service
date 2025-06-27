package fr.anisekai.web.controllers;

import fr.anisekai.library.Library;
import fr.anisekai.sanctum.interfaces.resolvers.StorageResolver;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.services.EpisodeService;
import fr.anisekai.server.services.TrackService;
import fr.anisekai.utils.DataUtils;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.media.enums.CodecType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping("/media")
public class MediaController {

    private final Library        library;
    private final AnimeService   animeService;
    private final EpisodeService episodeService;
    private final TrackService   trackService;

    public MediaController(Library library, AnimeService animeService, EpisodeService episodeService, TrackService trackService) {

        this.library        = library;
        this.animeService   = animeService;
        this.episodeService = episodeService;
        this.trackService   = trackService;
    }

    @GetMapping("/chunks/{episodeId:[0-9]+}/{name}")
    public ResponseEntity<InputStreamResource> getChunksContent(@PathVariable long episodeId, @PathVariable String name) throws IOException {

        Episode         episode  = this.episodeService.fetch(episodeId);
        StorageResolver resolver = this.library.getResolver(Library.CHUNKS);

        File target = resolver.file(episode, name).toFile();

        if (!target.exists()) return ResponseEntity.notFound().build();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(target));

        boolean   isDashMeta  = target.getName().endsWith(".mpd");
        MediaType dashMime    = MediaType.parseMediaType("application/dash+xml");
        MediaType defaultMime = MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentLength(target.length())
                .contentType(isDashMeta ? dashMime : defaultMime)
                .body(resource);
    }

    @GetMapping("/describe/{episodeId:[0-9]+}")
    public ResponseEntity<String> getEpisodeDescription(@PathVariable long episodeId) {

        Episode      episode = this.episodeService.fetch(episodeId);
        AnisekaiJson json    = new AnisekaiJson();
        json.put("mpd", "/media/chunks/%s/meta.mpd".formatted(episodeId));
        json.put("tracks", DataUtils.getTracksArray(episode));
        String data = json.toString();

        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(data);
    }

    @GetMapping("/subs/{trackId:[0-9]+}")
    public ResponseEntity<InputStreamResource> read(@PathVariable long trackId) throws FileNotFoundException {

        Track track = this.trackService.fetch(trackId);
        if (track.getCodec().getType() != CodecType.SUBTITLE) return ResponseEntity.badRequest().build();

        StorageResolver resolver = this.library.getResolver(Library.SUBTITLES);
        File            target   = resolver.file(track.getEpisode(), track.asFilename()).toFile();

        if (!target.exists()) return ResponseEntity.notFound().build();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(target));

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                             .contentLength(target.length())
                             .contentType(MediaType.parseMediaType(track.getCodec().getMimeType()))
                             .body(resource);
    }

    @GetMapping("/event-image/{animeId:[0-9]+}")
    public ResponseEntity<?> getImage(@PathVariable long animeId) throws IOException {

        Anime anime = this.animeService.fetch(animeId);

        StorageResolver resolver = this.library.getResolver(Library.EVENT_IMAGES);
        File            target   = resolver.file(anime).toFile();

        if (!target.exists()) {
            URI redirectUri = URI.create("/assets/images/unknown.webp");
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                                 .location(redirectUri)
                                 .build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(target));

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                             .contentLength(target.length())
                             .contentType(MediaType.IMAGE_PNG)
                             .body(resource);
    }


}
