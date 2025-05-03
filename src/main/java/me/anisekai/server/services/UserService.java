package me.anisekai.server.services;

import fr.anisekai.wireless.remote.interfaces.UserEntity;
import me.anisekai.server.entities.adapters.UserEventAdapter;
import me.anisekai.server.persistence.DataService;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.UserCreatedEvent;
import me.anisekai.server.proxy.UserProxy;
import me.anisekai.server.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class UserService extends DataService<DiscordUser, Long, UserEventAdapter, UserRepository, UserProxy> {

    public UserService(UserProxy proxy) {

        super(proxy);
    }

    public DiscordUser of(net.dv8tion.jda.api.entities.User user) {

        return this.getProxy().upsertEntity(
                user.getIdLong(),
                UserCreatedEvent::new,
                discordUser -> {
                    discordUser.setId(user.getIdLong());
                    discordUser.setUsername(user.getName());
                    discordUser.setAvatarUrl(user.getEffectiveAvatarUrl());
                    discordUser.setNickname(user.getGlobalName());
                }
        );
    }

    public Optional<DiscordUser> getByApiKey(String apiKey) {

        return this.getProxy().fetchEntity(repo -> repo.findByApiKey(apiKey));
    }

    public boolean canUseEmote(UserEntity requestingUser, String emote) {

        return this.fetchAll()
                   .stream()
                   .filter(user -> !Objects.isNull(user.getEmote()))
                   .filter(user -> !Objects.equals(user.getId(), requestingUser.getId()))
                   .noneMatch(user -> user.getEmote().equals(emote));
    }

    public UserEntity useEmote(UserEntity requestingUser, String emote) {

        return this.mod(requestingUser.getId(), this.defineEmote(emote));
    }

    public Consumer<UserEventAdapter> defineEmote(String emote) {

        return entity -> entity.setEmote(emote);
    }

    public List<DiscordUser> getActiveUsers() {

        return this.fetchAll(UserRepository::findAllByActiveIsTrue);
    }

}
