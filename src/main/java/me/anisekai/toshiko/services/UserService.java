package me.anisekai.toshiko.services;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.exceptions.users.EmojiAlreadyUsedException;
import me.anisekai.toshiko.exceptions.users.InvalidEmojiException;
import me.anisekai.toshiko.repositories.UserRepository;
import me.anisekai.toshiko.services.responses.SimpleResponse;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {

        this.repository = repository;
    }

    public DiscordUser retrieve(User user) {

        Optional<DiscordUser> optionalDiscordUser = this.repository.findById(user.getIdLong());
        DiscordUser           discordUser         = optionalDiscordUser.orElseGet(() -> new DiscordUser(user));
        return this.repository.save(discordUser);
    }

    public boolean swapEmoji(User user, String emoji) {

        if (emoji.matches("\\w*")) {
            throw new InvalidEmojiException();
        }

        DiscordUser discordUser = this.retrieve(user);

        if (emoji.equalsIgnoreCase(discordUser.getEmote())) {
            return false;
        }

        if (this.repository.findAll().stream().anyMatch(otherUser -> emoji.equals(otherUser.getEmote()))) {
            throw new EmojiAlreadyUsedException();
        }

        discordUser.setEmote(emoji);
        this.repository.save(discordUser);
        return true;
    }

}
