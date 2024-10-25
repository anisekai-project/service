package me.anisekai.modules.linn.services.proxy;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.events.anime.AnimeCreatedEvent;
import me.anisekai.modules.linn.exceptions.anime.AnimeNotFoundException;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.linn.repositories.AnimeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class AnimeProxyService extends ProxyService<Anime, Long, IAnime, AnimeRepository> {

    public AnimeProxyService(ApplicationEventPublisher publisher, AnimeRepository repository) {

        super(publisher, repository, Anime::new);
    }

    @Override
    public Anime getEntity(Function<AnimeRepository, Optional<Anime>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(AnimeNotFoundException::new);
    }

    public UpsertResult<Anime> upsert(String name, Consumer<IAnime> consumer) {

        return this.upsert(repository -> repository.findByName(name), anime -> {
            anime.setName(name);
            consumer.accept(anime);
        }, AnimeCreatedEvent::new);
    }

}
