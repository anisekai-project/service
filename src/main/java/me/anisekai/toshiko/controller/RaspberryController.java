package me.anisekai.toshiko.controller;

import me.anisekai.toshiko.controller.response.RaspberryData;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.helpers.JDAStore;
import me.anisekai.toshiko.helpers.RPC;
import me.anisekai.toshiko.repositories.AnimeNightRepository;
import me.anisekai.toshiko.repositories.AnimeRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/raspberry")
public class RaspberryController {

    private static final List<ScheduledEvent.Status> STATUSES = Arrays.asList(
            ScheduledEvent.Status.ACTIVE, ScheduledEvent.Status.SCHEDULED
    );

    private final RPC                  rpc;
    private final JDAStore             store;
    private final AnimeRepository      animeRepository;
    private final AnimeNightRepository animeNightRepository;

    public RaspberryController(RPC rpc, JDAStore store, AnimeRepository animeRepository, AnimeNightRepository animeNightRepository) {

        this.rpc                  = rpc;
        this.store                = store;
        this.animeRepository      = animeRepository;
        this.animeNightRepository = animeNightRepository;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public RaspberryData getData() throws Exception {

        if (!this.rpc.isReady() || this.store.getInstance().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY);
        }

        JDA                       jda            = this.store.requireInstance();
        List<Anime>               animes         = this.animeRepository.findAll();
        JSONObject                data           = this.rpc.getTorrents();
        JSONArray                 rawTorrentData = data.getJSONObject("arguments").getJSONArray("torrents");
        List<Map<String, Object>> torrents       = new ArrayList<>();
        for (int i = 0 ; i < rawTorrentData.length() ; i++) {
            torrents.add(rawTorrentData.getJSONObject(i).toMap());
        }

        List<RaspberryData.ServerEvent> events = this.animeNightRepository.findAllByStatusIn(STATUSES)
                                                                          .stream()
                                                                          .map(animeNight -> {
                                                                              ScheduledEvent event = jda.getScheduledEventById(animeNight.getId());
                                                                              return new RaspberryData.ServerEvent(animeNight, event);
                                                                          }).toList();

        return new RaspberryData(torrents, animes, events);
    }
}
