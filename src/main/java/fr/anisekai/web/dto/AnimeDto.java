package fr.anisekai.web.dto;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.wireless.remote.interfaces.EpisodeEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class AnimeDto {

    public long             id;
    public String           group;
    public byte             order;
    public String           title;
    public String           url;
    public String           imageUrl;
    public List<EpisodeDto> episodes;

    public AnimeDto(Anime anime, Collection<Episode> episodes) {

        this.id       = anime.getId();
        this.group    = anime.getGroup();
        this.order    = anime.getOrder();
        this.title    = anime.getTitle();
        this.url      = anime.getUrl();
        this.imageUrl = String.format("/media/event-image/%s", anime.getId());
        this.episodes = episodes.stream()
                                .sorted(Comparator.comparing(Episode::getNumber))
                                .filter(EpisodeEntity::isReady)
                                .map(EpisodeDto::new).toList();
    }

    public AnimeDto(Anime anime) {

        this.id       = anime.getId();
        this.group    = anime.getGroup();
        this.order    = anime.getOrder();
        this.title    = anime.getTitle();
        this.url      = anime.getUrl();
        this.imageUrl = String.format("/media/event-image/%s", anime.getId());
        this.episodes = new ArrayList<>();

        long amount = Math.abs(anime.getTotal());

        for (long i = 1; i <= amount; i++) {
            this.episodes.add(new EpisodeDto(i));
        }
    }


}
