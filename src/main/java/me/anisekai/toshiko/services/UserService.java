package me.anisekai.toshiko.services;


import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.user.UserEmoteUpdatedEvent;
import me.anisekai.toshiko.exceptions.users.EmoteAlreadyUsedException;
import me.anisekai.toshiko.exceptions.users.InvalidEmoteException;
import me.anisekai.toshiko.repositories.UserRepository;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository            repository;
    private final ApplicationEventPublisher publisher;

    public UserService(UserRepository repository, ApplicationEventPublisher publisher) {

        this.repository = repository;
        this.publisher  = publisher;
    }

    /**
     * Retrieve the updated {@link DiscordUser} for the provided {@link User}.
     *
     * @param user
     *         The Discord {@link User}
     *
     * @return The updated (or created) {@link DiscordUser}.
     */
    public DiscordUser get(User user) {

        DiscordUser discordUser = this.repository.findById(user.getIdLong())
                                                 .orElseGet(() ->


                                                 {
                                                     DiscordUser newUser = new DiscordUser();
                                                     newUser.setId(user.getIdLong());
                                                     newUser.setUsername(user.getName());
                                                     newUser.setDiscriminator("0000");
                                                     newUser.setActive(false);
                                                     newUser.setAdmin(false);
                                                     newUser.setWebAccess(false);
                                                     return newUser;
                                                 });

        discordUser.setId(user.getIdLong());
        discordUser.setUsername(user.getName());
        discordUser.setDiscriminator("0000");
        return this.repository.save(discordUser);
    }

    /**
     * Update the {@link DiscordUser} vote emote.
     *
     * @param user
     *         The {@link DiscordUser} for which the emote should be updated.
     * @param emote
     *         The emote to use.
     *
     * @return True if the emote has been updated, false if the emote was the same as the previous one.
     *
     * @throws InvalidEmoteException
     *         If the provided string isn't an emote.
     * @throws EmoteAlreadyUsedException
     *         Thrown if another {@link DiscordUser} is already using the provided emote.
     */
    public boolean setUserEmote(DiscordUser user, String emote) {

        LOGGER.info("setUserEmote: Updating User {} emote to {}", user.getId(), emote);

        if (emote.matches("\\w*")) {
            LOGGER.warn("{} is not a valid emote !", emote);
            throw new InvalidEmoteException();
        }

        if (emote.equals(user.getEmote())) {
            LOGGER.debug("User emote is already set to this value.");
            // No change to be done
            return false;
        }

        // Check if any user is already using the emote...
        if (
                this.repository.findAll()
                               .stream()
                               .anyMatch(discordUser -> emote.equals(discordUser.getEmote()))
        ) {
            LOGGER.warn("The emote is already used by another user !");
            throw new EmoteAlreadyUsedException();
        }

        user.setEmote(emote);
        DiscordUser saved = this.repository.save(user);
        LOGGER.debug("Sending UserEmoteUpdatedEvent...");
        this.publisher.publishEvent(new UserEmoteUpdatedEvent(this, saved));
        return true;
    }

    public void save(DiscordUser user) {
        this.repository.save(user);
    }

}
