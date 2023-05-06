package me.anisekai.toshiko.services.misc;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.Injection;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.services.UserService;
import net.dv8tion.jda.api.interactions.Interaction;
import org.springframework.stereotype.Service;

@Service
public class DiscordInjectionService {

    private final UserService userService;

    public DiscordInjectionService(UserService userService) {

        this.userService = userService;
    }

    public <T extends Interaction> Injection<DispatchEvent<T>, DiscordUser> entityUserMapper() {

        return event -> () -> this.userService.get(event.getInteraction().getUser());
    }
}
