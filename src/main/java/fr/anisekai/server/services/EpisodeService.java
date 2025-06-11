package fr.anisekai.server.services;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.adapters.EpisodeEventAdapter;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.EpisodeProxy;
import fr.anisekai.server.repositories.EpisodeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EpisodeService extends DataService<Episode, Long, EpisodeEventAdapter, EpisodeRepository, EpisodeProxy> {

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
