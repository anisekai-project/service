package me.anisekai.toshiko.controller;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.repositories.AnimeNightRepository;
import me.anisekai.toshiko.repositories.AnimeRepository;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/raspberry")
public class RaspberryController {

    private static final List<ScheduledEvent.Status> STATUSES = Arrays.asList(
            ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED
    );

    private static final Logger LOGGER = LoggerFactory.getLogger(RaspberryController.class);

    private final AnimeRepository animeRepository;
    private final AnimeNightRepository animeNightRepository;

    public RaspberryController(AnimeRepository animeRepository, AnimeNightRepository animeNightRepository) {

        this.animeRepository = animeRepository;
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
}
