package fr.anisekai.web.dto;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.wireless.remote.interfaces.EpisodeEntity;

import java.util.Comparator;
import java.util.List;

public class AnimeDto {

    public long             id;
    public String           group;
    public byte             order;
    public String           title;
    public List<EpisodeDto> episodes;

    public AnimeDto(Anime anime) {

        this.id       = anime.getId();
        this.group    = anime.getGroup();
        this.order    = anime.getOrder();
        this.title    = anime.getTitle();
        this.episodes = anime.getEpisodes().stream()
                             .sorted(Comparator.comparing(Episode::getNumber))
                             .filter(EpisodeEntity::isReady)
                             .map(EpisodeDto::new).toList();
    }

}
