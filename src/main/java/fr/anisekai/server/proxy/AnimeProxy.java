package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.adapters.AnimeEventAdapter;
import fr.anisekai.server.events.AnimeCreatedEvent;
import fr.anisekai.server.exceptions.anime.AnimeNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.AnimeRepository;
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
