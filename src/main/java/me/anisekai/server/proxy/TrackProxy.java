package me.anisekai.server.proxy;

import me.anisekai.server.entities.Track;
import me.anisekai.server.entities.adapters.TrackEventAdapter;
import me.anisekai.server.events.TrackCreatedEvent;
import me.anisekai.server.exceptions.track.TrackNotFoundException;
import me.anisekai.server.persistence.ProxyService;
import me.anisekai.server.repositories.TrackRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TrackProxy extends ProxyService<Track, Long, TrackEventAdapter, TrackRepository> {

    public TrackProxy(ApplicationEventPublisher publisher, TrackRepository repository) {

        super(publisher, repository, Track::new);
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
    public Track getEntity(Function<TrackRepository, Optional<Track>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(TrackNotFoundException::new);
    }

    public Track create(Consumer<TrackEventAdapter> consumer) {

        return this.create(consumer, TrackCreatedEvent::new);
    }

}
