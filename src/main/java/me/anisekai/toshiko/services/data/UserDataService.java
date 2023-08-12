package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.interfaces.entities.IUser;
import me.anisekai.toshiko.repositories.UserRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.proxy.UserProxyService;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class UserDataService extends AbstractDataService<DiscordUser, Long, IUser, UserRepository, UserProxyService> {

    public UserDataService(UserProxyService proxy) {

        super(proxy);
    }

    public List<DiscordUser> getActive() {

        return this.getProxy().fetchEntities(UserRepository::findAllByActiveIsTrue);
    }

    public boolean isEmoteInUse(String emote) {

        return this.getProxy().fetchEntities(UserRepository::findAll)
                   .stream()
                   .anyMatch(user -> Objects.equals(emote, user.getEmote()));
    }

    public DiscordUser getFrom(User jdaUser) {

        return this.getProxy().upsert(jdaUser.getIdLong(), this.update(jdaUser)).result();
    }

    public Consumer<IUser> promote() {

        return user -> {
            user.setActive(true);
            user.setWebAccess(true);
        };
    }

    public Consumer<IUser> demote() {

        return user -> {
            user.setActive(false);
            user.setWebAccess(false);
        };
    }

    public Consumer<IUser> update(User jdaUser) {

        return user -> {
            user.setUsername(jdaUser.getName());
            user.setDiscriminator(jdaUser.getDiscriminator());
        };
    }


}
