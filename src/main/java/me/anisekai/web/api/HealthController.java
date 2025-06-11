package me.anisekai.web.api;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import me.anisekai.server.repositories.AnimeRepository;
import me.anisekai.server.repositories.BroadcastRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v3/health")
public class HealthController {

    private final AnimeRepository     animeRepository;
    private final BroadcastRepository broadcastRepository;

    public HealthController(AnimeRepository animeRepository, BroadcastRepository broadcastRepository) {

        this.animeRepository     = animeRepository;
        this.broadcastRepository = broadcastRepository;
    }

    @GetMapping("/")
    public String getHealth() {

        ZonedDateTime now = ZonedDateTime.now();

        AnisekaiJson data  = new AnisekaiJson();
        AnisekaiJson stats = new AnisekaiJson();

        stats.put("animes", this.animeRepository.count());
        stats.put("upcoming_broadcasts", this.broadcastRepository.countBroadcastByStartingAtAfter(now));

        data.put("status", "ok");
        data.put("stats", stats);

        return data.toString();
    }

}
