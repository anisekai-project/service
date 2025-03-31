package me.anisekai.server.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.DiscordUserCreatedEvent;
import me.anisekai.server.exceptions.discorduser.DiscordUserNotFoundException;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.repositories.DiscordUserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class DiscordUserProxy extends ProxyService<DiscordUser, Long, IDiscordUser, DiscordUserRepository> {

    public DiscordUserProxy(ApplicationEventPublisher publisher, DiscordUserRepository repository) {

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
    public DiscordUser getEntity(Function<DiscordUserRepository, Optional<DiscordUser>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(DiscordUserNotFoundException::new);
    }

    public DiscordUser create(Consumer<IDiscordUser> consumer) {

        return this.create(consumer, DiscordUserCreatedEvent::new);
    }

}
