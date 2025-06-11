package me.anisekai.server.proxy;

import me.anisekai.server.entities.Selection;
import me.anisekai.server.entities.adapters.SelectionEventAdapter;
import me.anisekai.server.events.SelectionCreatedEvent;
import me.anisekai.server.exceptions.selection.SelectionNotFoundException;
import me.anisekai.server.persistence.ProxyService;
import me.anisekai.server.repositories.SelectionRepository;
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
