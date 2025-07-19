package fr.anisekai.web.api;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.repositories.AnimeRepository;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.web.data.Session;
import fr.anisekai.web.annotations.RequireAuth;
import fr.anisekai.web.dto.AnimeDto;
import fr.anisekai.web.enums.SessionType;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/animes")
public class AnimeController {

    private final AnimeService animeService;

    public AnimeController(AnimeService animeService) {

        this.animeService = animeService;
    }

    @Deprecated
    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequireAuth(allowedSessionTypes = SessionType.APP)
    @Operation(summary = "[Deprecated] Import an anime", description = "This endpoint is used in the browser extension and should not be used for any other purpose.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Import successful."),
            @ApiResponse(responseCode = "500", description = "Import failure.")
    })
    public String importAnime(Session session, @RequestBody String rawJson) {

        var result = this.animeService.importAnime(session.getIdentity(), new AnisekaiJson(rawJson));

        return new AnisekaiJson()
                .putInTree("result.success", true)
                .putInTree("result.state", result.action().name())
                .toString();
    }

    @RequireAuth(allowGuests = false)
    @GetMapping(path = "/watchable", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve watchable anime", description = "Allow to retrieve all animes with at least one watchable episode.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieval successful"),
            @ApiResponse(responseCode = "204", description = "Nothing to retrieve", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<List<AnimeDto>> listWatchableAnime() {

        List<Anime> animes = this.animeService.fetchAll(AnimeRepository::findByEpisodeReady);
        if (animes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(AnimeDto.toSortedDtos(animes, anime -> new AnimeDto(anime, anime.getEpisodes())));
    }


    @RequireAuth(allowGuests = false, requireAdmin = true)
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve all anime", description = "Allow to retrieve all animes from the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieval successful"),
            @ApiResponse(responseCode = "204", description = "Nothing to retrieve", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<List<AnimeDto>> listAllAnimes() {

        List<Anime> animes = this.animeService.fetchAll();
        if (animes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(AnimeDto.toSortedDtos(animes, AnimeDto::new));
    }

}
