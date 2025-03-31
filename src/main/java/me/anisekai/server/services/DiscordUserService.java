package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.DiscordUserCreatedEvent;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.proxy.DiscordUserProxy;
import me.anisekai.server.repositories.DiscordUserRepository;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class DiscordUserService extends DataService<DiscordUser, Long, IDiscordUser, DiscordUserRepository, DiscordUserProxy> {

    public DiscordUserService(DiscordUserProxy proxy) {

        super(proxy);
    }

    public DiscordUser of(User user) {

        return this.getProxy().upsertEntity(
                user.getIdLong(),
                DiscordUserCreatedEvent::new,
                discordUser -> discordUser.setUsername(user.getName())
        );
    }

    public boolean canUseEmote(IDiscordUser requestingUser, String emote) {

        return this.fetchAll()
                   .stream()
                   .filter(user -> !Objects.isNull(user.getEmote()))
                   .filter(user -> !Objects.equals(user.getId(), requestingUser.getId()))
                   .noneMatch(user -> user.getEmote().equals(emote));
    }

    public IDiscordUser useEmote(IDiscordUser requestingUser, String emote) {

        return this.mod(requestingUser.getId(), this.defineEmote(emote));
    }

    public Consumer<IDiscordUser> defineEmote(String emote) {

        return entity -> entity.setEmote(emote);
    }

    public List<DiscordUser> getActiveUsers() {

        return this.fetchAll(DiscordUserRepository::findAllByActiveIsTrue);
    }

}
