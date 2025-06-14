package fr.anisekai.web.controllers;

import fr.anisekai.library.LibraryService;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
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

@Controller
@RequestMapping("/media")
public class MediaController {

    private final LibraryService libraryService;
    private final EpisodeService episodeService;
    private final TrackService   trackService;

    public MediaController(LibraryService libraryService, EpisodeService episodeService, TrackService trackService) {

        this.libraryService = libraryService;
        this.episodeService = episodeService;
        this.trackService   = trackService;
    }

    @GetMapping("/chunks/{episodeId:[0-9]+}/{name}")
    public ResponseEntity<InputStreamResource> getChunksContent(@PathVariable long episodeId, @PathVariable String name) throws FileNotFoundException {

        Episode episode       = this.episodeService.fetch(episodeId);
        File    chunksStore   = this.libraryService.getChunksStore(episode);
        File    requestedFile = new File(chunksStore, name);

        if (!requestedFile.exists()) return ResponseEntity.notFound().build();

        if (!this.libraryService.isWithinLibrary(requestedFile)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(requestedFile));

        if (name.equals("meta.mdp")) {
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
        File  file  = this.libraryService.getSubFile(track);

        if (track.getCodec().getType() != CodecType.SUBTITLE) return ResponseEntity.badRequest().build();
        if (!file.exists()) return ResponseEntity.notFound().build();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                             .contentLength(file.length())
                             .contentType(MediaType.parseMediaType(track.getCodec().getMimeType()))
                             .body(resource);
    }

}
