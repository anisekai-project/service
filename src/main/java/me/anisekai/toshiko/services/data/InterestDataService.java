package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.entities.keys.UserAnimeAssocKey;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.IInterest;
import me.anisekai.toshiko.repositories.InterestRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.proxy.InterestProxyService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InterestDataService extends AbstractDataService<Interest, UserAnimeAssocKey, IInterest, InterestRepository, InterestProxyService> {

    public InterestDataService(InterestProxyService proxy) {

        super(proxy);
    }

    public Optional<UpsertResult<Interest>> setInterest(DiscordUser user, Anime anime, InterestLevel level) {

        UserAnimeAssocKey id       = new UserAnimeAssocKey(anime, user);

        if (this.getProxy().fetchEntity(id).map(interest -> interest.getLevel() == level).orElse(false)) {
            return Optional.empty();
        }

        return Optional.of(this.getProxy().upsert(user, anime, interest -> interest.setLevel(level)));
    }

}