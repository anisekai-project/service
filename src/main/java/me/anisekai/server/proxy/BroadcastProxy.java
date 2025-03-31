package me.anisekai.server.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.events.BroadcastCreatedEvent;
import me.anisekai.server.exceptions.broadcast.BroadcastNotFoundException;
import me.anisekai.server.interfaces.IBroadcast;
import me.anisekai.server.repositories.BroadcastRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class BroadcastProxy extends ProxyService<Broadcast, Long, IBroadcast<Anime>, BroadcastRepository> {

    public BroadcastProxy(ApplicationEventPublisher publisher, BroadcastRepository repository) {

        super(publisher, repository, Broadcast::new);
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
    public Broadcast getEntity(Function<BroadcastRepository, Optional<Broadcast>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(BroadcastNotFoundException::new);
    }

    public Broadcast create(Consumer<IBroadcast<Anime>> consumer) {

        return this.create(consumer, BroadcastCreatedEvent::new);
    }

}
