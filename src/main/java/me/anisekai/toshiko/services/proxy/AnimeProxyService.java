package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.anime.AnimeCreatedEvent;
import me.anisekai.toshiko.exceptions.anime.AnimeNotFoundException;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.IAnime;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class AnimeProxyService extends AbstractProxyService<Anime, Long, IAnime, AnimeRepository> {

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
