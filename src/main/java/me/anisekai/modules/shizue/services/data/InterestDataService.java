package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.Interest;
import me.anisekai.modules.shizue.entities.keys.UserAnimeAssocKey;
import me.anisekai.modules.shizue.enums.InterestLevel;
import me.anisekai.modules.shizue.interfaces.entities.IInterest;
import me.anisekai.modules.shizue.repositories.InterestRepository;
import me.anisekai.modules.shizue.services.proxy.InterestProxyService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InterestDataService extends DataService<Interest, UserAnimeAssocKey, IInterest, InterestRepository, InterestProxyService> {

    public InterestDataService(InterestProxyService proxy) {

        super(proxy);
    }

    public Optional<UpsertResult<Interest>> setInterest(DiscordUser user, Anime anime, InterestLevel level) {

        UserAnimeAssocKey id = new UserAnimeAssocKey(anime, user);

        if (this.getProxy().fetchEntity(id).map(interest -> interest.getLevel() == level).orElse(false)) {
            return Optional.empty();
        }

        return Optional.of(this.getProxy().upsert(user, anime, interest -> interest.setLevel(level)));
    }

}