package fr.anisekai.web.dto;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.wireless.remote.interfaces.EpisodeEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class AnimeDto {

    private static final String IMAGE_URL = "/api/v3/library/event-images/%s";

    public final long             id;
    public final String           group;
    public final byte             order;
    public final String           title;
    public final String           url;
    public final String           imageUrl;
    public final List<EpisodeDto> episodes;

    public AnimeDto(Anime anime, Collection<Episode> episodes) {

        this.id       = anime.getId();
        this.group    = anime.getGroup();
        this.order    = anime.getOrder();
        this.title    = anime.getTitle();
        this.url      = anime.getUrl();
        this.imageUrl = String.format(IMAGE_URL, anime.getId());
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
        this.imageUrl = String.format(IMAGE_URL, anime.getId());
        this.episodes = new ArrayList<>();

        int amount = Math.abs(anime.getTotal());

        for (int i = 1; i <= amount; i++) {
            this.episodes.add(new EpisodeDto(i));
        }
    }

    public static List<AnimeDto> toSortedDtos(Collection<Anime> animes, Function<Anime, AnimeDto> dtoFunction) {

        return animes.stream()
                     .sorted(Comparator.comparing(Anime::getGroup).thenComparing(Anime::getOrder))
                     .map(dtoFunction)
                     .toList();
    }

}
