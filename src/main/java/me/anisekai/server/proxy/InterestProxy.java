package me.anisekai.server.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.events.InterestCreatedEvent;
import me.anisekai.server.exceptions.interest.InterestNotFoundException;
import me.anisekai.server.interfaces.IInterest;
import me.anisekai.server.keys.UserAnimeKey;
import me.anisekai.server.repositories.InterestRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class InterestProxy extends ProxyService<Interest, UserAnimeKey, IInterest<DiscordUser, Anime>, InterestRepository> {

    public InterestProxy(ApplicationEventPublisher publisher, InterestRepository repository) {

        super(publisher, repository, Interest::new);
    }

    /**
     * Same as {@link #fetchEntity(Function)} but should ensure that the selector should not return any empty optional
     * instance by throwing any {@link RuntimeException} using {@link Optional#orElseThrow(Supplier)}.
     *
     * @param selector
     *         The selector to use to retrieve the entity.
     *
     * @return The entity instance.
     */
    @Override
    public Interest getEntity(Function<InterestRepository, Optional<Interest>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(InterestNotFoundException::new);
    }

    public Interest create(Consumer<IInterest<DiscordUser, Anime>> consumer) {

        return this.create(consumer, InterestCreatedEvent::new);
    }

}
