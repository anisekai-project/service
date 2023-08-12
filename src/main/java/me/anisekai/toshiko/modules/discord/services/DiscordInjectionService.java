package me.anisekai.toshiko.modules.discord.services;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.Injection;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.interfaces.entities.IUser;
import me.anisekai.toshiko.services.data.UserDataService;
import net.dv8tion.jda.api.interactions.Interaction;
import org.springframework.stereotype.Service;

@Service
public class DiscordInjectionService {

    private final UserDataService userService;

    public DiscordInjectionService(UserDataService userService) {

        this.userService = userService;
    }

    public <T extends Interaction> Injection<DispatchEvent<T>, DiscordUser> entityUserMapper() {

        return event -> () -> this.userService.getFrom(event.getInteraction().getUser());
    }

    public <T extends Interaction> Injection<DispatchEvent<T>, IUser> entityUserInterfaceMapper() {

        return event -> () -> this.userService.getFrom(event.getInteraction().getUser());
    }

}
