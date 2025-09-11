package fr.anisekai.server.services;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.entities.adapters.UserEventAdapter;
import fr.anisekai.server.events.UserCreatedEvent;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.UserProxy;
import fr.anisekai.server.repositories.UserRepository;
import fr.anisekai.web.packets.results.DiscordIdentity;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import net.dv8tion.jda.api.entities.User;
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

    public DiscordUser of(User user) {

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

    public DiscordUser ensureUserExists(DiscordIdentity identity) {

        return this.getProxy()
                   .fetchEntity(identity.getId())
                   .orElseGet(() -> this.getProxy().create(user -> {
                       user.setId(identity.getId());
                       user.setUsername(identity.getGlobalName());
                       user.setNickname(identity.getUsername());
                       user.setAvatarUrl(identity.getAvatar());
                   }));

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

    public Optional<DiscordUser> findFromIdentity(DiscordIdentity identity) {

        return this.getProxy().fetchEntity(identity.getId());
    }

}
