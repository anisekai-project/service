package me.anisekai.discord;

import fr.alexpado.jda.interactions.InteractionExtension;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.Injection;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutoCompleteProvider;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.enums.BroadcastFrequency;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.services.AnimeService;
import me.anisekai.server.services.DiscordUserService;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class InteractionService {

    private final DiscordUserService userService;
    private final AnimeService       animeService;

    public InteractionService(DiscordUserService userService, AnimeService animeService) {

        this.userService  = userService;
        this.animeService = animeService;
    }

    public void using(InteractionExtension extension, Map<String, AutoCompleteProvider> completionMap) {

        extension.getSlashContainer().addClassMapping(IDiscordUser.class, this.entityUserInterfaceMapper());
        extension.getSlashContainer().addClassMapping(DiscordUser.class, this.entityUserMapper());
        extension.getButtonContainer().addClassMapping(IDiscordUser.class, this.entityUserInterfaceMapper());
        extension.getButtonContainer().addClassMapping(DiscordUser.class, this.entityUserMapper());

        completionMap.put("anime", this::animeCompletion);
        completionMap.put("watchlist", this::watchlistCompletion);
        completionMap.put("frequency", this::frequencyCompletion);
    }

    private <T extends Interaction> Injection<DispatchEvent<T>, DiscordUser> entityUserMapper() {

        return (event, option) -> () -> this.userService.of(event.getInteraction().getUser());
    }

    private <T extends Interaction> Injection<DispatchEvent<T>, IDiscordUser> entityUserInterfaceMapper() {

        return (event, option) -> () -> this.userService.of(event.getInteraction().getUser());
    }

    private List<Command.Choice> animeCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return this.animeService
                .fetchAll()
                .stream()
                .filter(anime -> anime.getTitle().toLowerCase().contains(value.toLowerCase()))
                .sorted()
                .map(anime -> new Command.Choice(
                        StringUtils.truncate(
                                String.format("%s %s", anime.getWatchlist().getIcon(), anime.getTitle()),
                                100
                        ),
                        anime.getId()
                ))
                .toList();
    }

    private List<Command.Choice> watchlistCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return Stream.of(AnimeStatus.values())
                     .filter(status -> status.getDisplay().toLowerCase().contains(value.toLowerCase()))
                     .map(status -> new Command.Choice(status.getDisplay(), status.name()))
                     .toList();
    }

    private List<Command.Choice> frequencyCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return Stream.of(BroadcastFrequency.values())
                     .filter(frequency -> frequency.getDisplayName().toLowerCase().contains(value.toLowerCase()))
                     .map(frequency -> new Command.Choice(frequency.getDisplayName(), frequency.name()))
                     .toList();
    }

}
