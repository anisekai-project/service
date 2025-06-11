package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.entities.adapters.BroadcastEventAdapter;
import fr.anisekai.server.events.BroadcastCreatedEvent;
import fr.anisekai.server.exceptions.broadcast.BroadcastNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.BroadcastRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class BroadcastProxy extends ProxyService<Broadcast, Long, BroadcastEventAdapter, BroadcastRepository> {

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

    public Broadcast create(Consumer<BroadcastEventAdapter> consumer) {

        return this.create(consumer, BroadcastCreatedEvent::new);
    }

}
