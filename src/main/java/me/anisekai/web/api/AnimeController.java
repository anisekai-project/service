package me.anisekai.web.api;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.services.AnimeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/animes")
public class AnimeController {

    private final AnimeService animeService;

    public AnimeController(AnimeService animeService) {

        this.animeService = animeService;
    }

    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String importAnime(DiscordUser user, @RequestBody String rawJson) {

        var result = this.animeService.importAnime(user, new AnisekaiJson(rawJson));

        return new AnisekaiJson()
                .putInTree("result.success", true)
                .putInTree("result.state", result.action().name())
                .toString();
    }

}
