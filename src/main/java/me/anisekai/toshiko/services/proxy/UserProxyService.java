package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.user.UserCreatedEvent;
import me.anisekai.toshiko.exceptions.user.UserNotFoundException;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.IUser;
import me.anisekai.toshiko.repositories.UserRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class UserProxyService extends AbstractProxyService<DiscordUser, Long, IUser, UserRepository> {

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
