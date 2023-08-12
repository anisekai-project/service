package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.entities.keys.UserAnimeAssocKey;
import me.anisekai.toshiko.events.interest.InterestCreatedEvent;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.IInterest;
import me.anisekai.toshiko.repositories.InterestRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class InterestProxyService extends AbstractProxyService<Interest, UserAnimeAssocKey, IInterest, InterestRepository> {

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
