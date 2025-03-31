package me.anisekai.server.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Media;
import me.anisekai.server.events.MediaCreatedEvent;
import me.anisekai.server.exceptions.media.MediaNotFoundException;
import me.anisekai.server.interfaces.IMedia;
import me.anisekai.server.repositories.MediaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class MediaProxy extends ProxyService<Media, Long, IMedia<Episode>, MediaRepository> {

    public MediaProxy(ApplicationEventPublisher publisher, MediaRepository repository) {

        super(publisher, repository, Media::new);
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
    public Media getEntity(Function<MediaRepository, Optional<Media>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(MediaNotFoundException::new);
    }

    public Media create(Consumer<IMedia<Episode>> consumer) {

        return this.create(consumer, MediaCreatedEvent::new);
    }

}
