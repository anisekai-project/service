package me.anisekai.server.proxy;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.adapters.AnimeEventAdapter;
import me.anisekai.server.events.AnimeCreatedEvent;
import me.anisekai.server.exceptions.anime.AnimeNotFoundException;
import me.anisekai.server.persistence.ProxyService;
import me.anisekai.server.repositories.AnimeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class AnimeProxy extends ProxyService<Anime, Long, AnimeEventAdapter, AnimeRepository> {

    public AnimeProxy(ApplicationEventPublisher publisher, AnimeRepository repository) {

        super(publisher, repository, Anime::new);
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
    public Anime getEntity(Function<AnimeRepository, Optional<Anime>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(AnimeNotFoundException::new);
    }

    public Anime create(Consumer<AnimeEventAdapter> consumer) {

        return this.create(consumer, AnimeCreatedEvent::new);
    }


}
