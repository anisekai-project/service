package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.entities.adapters.UserEventAdapter;
import fr.anisekai.server.events.UserCreatedEvent;
import fr.anisekai.server.exceptions.user.UserNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class UserProxy extends ProxyService<DiscordUser, Long, UserEventAdapter, UserRepository> {

    public UserProxy(ApplicationEventPublisher publisher, UserRepository repository) {

        super(publisher, repository, DiscordUser::new);
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
    public DiscordUser getEntity(Function<UserRepository, Optional<DiscordUser>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(UserNotFoundException::new);
    }

    public DiscordUser create(Consumer<UserEventAdapter> consumer) {

        return this.create(consumer, UserCreatedEvent::new);
    }

}
