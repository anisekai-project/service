package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.entities.adapters.SelectionEventAdapter;
import fr.anisekai.server.events.SelectionCreatedEvent;
import fr.anisekai.server.exceptions.selection.SelectionNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.SelectionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class SelectionProxy extends ProxyService<Selection, Long, SelectionEventAdapter, SelectionRepository> {

    public SelectionProxy(ApplicationEventPublisher publisher, SelectionRepository repository) {

        super(publisher, repository, Selection::new);
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
    public Selection getEntity(Function<SelectionRepository, Optional<Selection>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SelectionNotFoundException::new);
    }

    public Selection create(Consumer<SelectionEventAdapter> consumer) {

        return this.create(consumer, SelectionCreatedEvent::new);
    }

}
