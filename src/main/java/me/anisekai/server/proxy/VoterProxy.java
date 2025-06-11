package me.anisekai.server.proxy;

import fr.anisekai.wireless.remote.keys.VoterKey;
import me.anisekai.server.entities.Voter;
import me.anisekai.server.entities.adapters.VoterEventAdapter;
import me.anisekai.server.events.VoterCreatedEvent;
import me.anisekai.server.exceptions.voter.VoterNotFoundException;
import me.anisekai.server.persistence.ProxyService;
import me.anisekai.server.repositories.VoterRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class VoterProxy extends ProxyService<Voter, VoterKey, VoterEventAdapter, VoterRepository> {

    public VoterProxy(ApplicationEventPublisher publisher, VoterRepository repository) {

        super(publisher, repository, Voter::new);
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
    public Voter getEntity(Function<VoterRepository, Optional<Voter>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(VoterNotFoundException::new);
    }

    public Voter create(Consumer<VoterEventAdapter> consumer) {

        return this.create(consumer, VoterCreatedEvent::new);
    }

}
