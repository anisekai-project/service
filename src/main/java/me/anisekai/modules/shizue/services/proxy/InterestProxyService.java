package me.anisekai.modules.shizue.services.proxy;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.Interest;
import me.anisekai.modules.shizue.entities.keys.UserAnimeAssocKey;
import me.anisekai.modules.shizue.events.interest.InterestCreatedEvent;
import me.anisekai.modules.shizue.interfaces.entities.IInterest;
import me.anisekai.modules.shizue.repositories.InterestRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class InterestProxyService extends ProxyService<Interest, UserAnimeAssocKey, IInterest, InterestRepository> {

    public InterestProxyService(ApplicationEventPublisher publisher, InterestRepository repository) {

        super(publisher, repository, Interest::new);
    }

    @Override
    public Interest getEntity(Function<InterestRepository, Optional<Interest>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(() -> new IllegalStateException("-TODO"));
    }

    public UpsertResult<Interest> upsert(DiscordUser user, Anime anime, Consumer<IInterest> consumer) {

        UserAnimeAssocKey id = new UserAnimeAssocKey(anime, user);

        return this.upsert(repository -> repository.findById(id), interest -> {
            interest.setAnime(anime);
            interest.setUser(user);
            consumer.accept(interest);
        }, InterestCreatedEvent::new);
    }

}
