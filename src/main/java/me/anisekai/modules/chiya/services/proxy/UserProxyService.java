package me.anisekai.modules.chiya.services.proxy;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.chiya.events.user.UserCreatedEvent;
import me.anisekai.modules.chiya.exceptions.user.UserNotFoundException;
import me.anisekai.modules.chiya.interfaces.IUser;
import me.anisekai.modules.chiya.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class UserProxyService extends ProxyService<DiscordUser, Long, IUser, UserRepository> {

    public UserProxyService(ApplicationEventPublisher publisher, UserRepository repository) {

        super(publisher, repository, DiscordUser::new);
    }

    @Override
    public DiscordUser getEntity(Function<UserRepository, Optional<DiscordUser>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(UserNotFoundException::new);
    }

    public UpsertResult<DiscordUser> upsert(long id, Consumer<IUser> consumer) {

        return this.upsert(repository -> repository.findById(id), user -> {
            user.setId(id);
            consumer.accept(user);
        }, UserCreatedEvent::new);
    }

}
