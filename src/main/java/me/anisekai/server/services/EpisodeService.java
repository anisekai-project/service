package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.interfaces.IEpisode;
import me.anisekai.server.proxy.EpisodeProxy;
import me.anisekai.server.repositories.EpisodeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EpisodeService extends DataService<Episode, Long, IEpisode<Anime>, EpisodeRepository, EpisodeProxy> {

    public EpisodeService(EpisodeProxy proxy) {

        super(proxy);
    }

    public Optional<Episode> getEpisode(Anime anime, int number) {

        return this.getProxy().fetchEntity(repo -> repo.findByAnimeAndNumber(anime, number));
    }

    public Episode create(Anime anime, int number) {

        return this.getProxy().create(episode -> {
            episode.setAnime(anime);
            episode.setNumber(number);
        });
    }

}
