package me.anisekai.server.services;

import fr.anisekai.wireless.remote.keys.InterestKey;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.entities.adapters.InterestEventAdapter;
import me.anisekai.server.events.InterestCreatedEvent;
import me.anisekai.server.persistence.DataService;
import me.anisekai.server.proxy.InterestProxy;
import me.anisekai.server.repositories.InterestRepository;
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
