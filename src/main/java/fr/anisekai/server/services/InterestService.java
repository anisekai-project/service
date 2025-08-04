package fr.anisekai.server.services;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.entities.Interest;
import fr.anisekai.server.entities.adapters.InterestEventAdapter;
import fr.anisekai.server.events.InterestCreatedEvent;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.InterestProxy;
import fr.anisekai.server.repositories.InterestRepository;
import fr.anisekai.wireless.remote.keys.InterestKey;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class InterestService extends DataService<Interest, InterestKey, InterestEventAdapter, InterestRepository, InterestProxy> {

    public InterestService(InterestProxy proxy) {

        super(proxy);
    }

    public List<Interest> getInterests(Anime anime) {

        return this.fetchAll(repo -> repo.findByAnime(anime));
    }

    public List<Interest> getInterests(Collection<Anime> animes) {

        return this.fetchAll(repo -> repo.findByAnimeIn(animes));
    }

    public List<Interest> getInterests(DiscordUser user) {

        return this.fetchAll(repo -> repo.findByUser(user));
    }

    public void setInterest(DiscordUser user, Anime anime, byte level) {

        InterestKey key = new InterestKey(anime.getId(), user.getId());

        this.getProxy().upsert(
                repo -> repo.findById(key),
                entity -> {
                    entity.setUser(user);
                    entity.setAnime(anime);
                    entity.setLevel(level);
                },
                InterestCreatedEvent::new
        );
    }

}
