package me.anisekai.modules.shizue.services.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.events.broadcast.BroadcastCreatedEvent;
import me.anisekai.modules.shizue.exceptions.broadcast.BroadcastNotFoundException;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.modules.shizue.repositories.BroadcastRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class BroadcastProxyService extends ProxyService<Broadcast, Long, IBroadcast, BroadcastRepository> {

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
