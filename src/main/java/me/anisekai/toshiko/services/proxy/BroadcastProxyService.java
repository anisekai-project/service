package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.broadcast.BroadcastCreatedEvent;
import me.anisekai.toshiko.exceptions.broadcast.BroadcastNotFoundException;
import me.anisekai.toshiko.interfaces.entities.IBroadcast;
import me.anisekai.toshiko.repositories.BroadcastRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class BroadcastProxyService extends AbstractProxyService<Broadcast, Long, IBroadcast, BroadcastRepository> {

    public BroadcastProxyService(ApplicationEventPublisher publisher, BroadcastRepository repository) {

        super(publisher, repository, Broadcast::new);
    }

    @Override
    public Broadcast getEntity(Function<BroadcastRepository, Optional<Broadcast>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(BroadcastNotFoundException::new);
    }

    public Broadcast create(Consumer<IBroadcast> consumer) {

        return this.create(consumer, BroadcastCreatedEvent::new);
    }

}
