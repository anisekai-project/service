package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Interest;
import fr.anisekai.server.entities.adapters.InterestEventAdapter;
import fr.anisekai.server.events.InterestCreatedEvent;
import fr.anisekai.server.exceptions.interest.InterestNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.InterestRepository;
import fr.anisekai.wireless.remote.keys.InterestKey;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class InterestProxy extends ProxyService<Interest, InterestKey, InterestEventAdapter, InterestRepository> {

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

    public Interest create(Consumer<InterestEventAdapter> consumer) {

        return this.create(consumer, InterestCreatedEvent::new);
    }

}
