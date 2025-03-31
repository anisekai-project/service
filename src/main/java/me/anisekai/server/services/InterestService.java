package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.events.InterestCreatedEvent;
import me.anisekai.server.interfaces.IInterest;
import me.anisekai.server.keys.UserAnimeKey;
import me.anisekai.server.proxy.InterestProxy;
import me.anisekai.server.repositories.InterestRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class InterestService extends DataService<Interest, UserAnimeKey, IInterest<DiscordUser, Anime>, InterestRepository, InterestProxy> {

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

    public void setInterest(DiscordUser user, Anime anime, long level) {

        UserAnimeKey key = new UserAnimeKey(anime.getId(), user.getId());

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
