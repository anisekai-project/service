package fr.anisekai.web.controllers;

import fr.anisekai.library.LibraryService;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.services.EpisodeService;
import fr.anisekai.server.services.TrackService;
import fr.anisekai.utils.DataUtils;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.media.enums.CodecType;
import org.springframework.core.io.ClassPathResource;
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

@Controller
@RequestMapping("/media")
public class MediaController {

    private final LibraryService libraryService;
    private final AnimeService   animeService;
    private final EpisodeService episodeService;
    private final TrackService   trackService;

    public MediaController(LibraryService libraryService, AnimeService animeService, EpisodeService episodeService, TrackService trackService) {

        this.libraryService = libraryService;
        this.animeService   = animeService;
        this.episodeService = episodeService;
        this.trackService   = trackService;
    }

    @GetMapping("/chunks/{episodeId:[0-9]+}/{name}")
    public ResponseEntity<InputStreamResource> getChunksContent(@PathVariable long episodeId, @PathVariable String name) throws IOException {

        Episode episode       = this.episodeService.fetch(episodeId);
        File    requestedFile = this.libraryService.retrieveEpisodeChunk(episode, name);

        if (!requestedFile.exists()) return ResponseEntity.notFound().build();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(requestedFile));

        if (requestedFile.getName().equals("meta.mpd")) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentLength(requestedFile.length())
                    .contentType(MediaType.parseMediaType("application/dash+xml"))
                    .body(resource);
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentLength(requestedFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
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
        File  file  = this.libraryService.retrieveSubtitle(track);

        if (track.getCodec().getType() != CodecType.SUBTITLE) return ResponseEntity.badRequest().build();
        if (!file.exists()) return ResponseEntity.notFound().build();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                             .contentLength(file.length())
                             .contentType(MediaType.parseMediaType(track.getCodec().getMimeType()))
                             .body(resource);
    }

    @GetMapping("/event-image/{animeId:[0-9]+}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable long animeId) throws IOException {

        Anime anime = this.animeService.fetch(animeId);
        File  file  = this.libraryService.retrieveAnimeEventImage(anime);

        InputStreamResource resource;
        long                length;

        if (file.exists()) {
            resource = new InputStreamResource(new FileInputStream(file));
            length   = file.length();
        } else {
            // Load the default image from classpath
            ClassPathResource defaultImage = new ClassPathResource("static/assets/images/unknown.png");
            resource = new InputStreamResource(defaultImage.getInputStream());
            length   = defaultImage.contentLength();
        }

        return ResponseEntity.status(file.exists() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                             .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                             .contentLength(length)
                             .contentType(MediaType.IMAGE_PNG)
                             .body(resource);
    }


}
