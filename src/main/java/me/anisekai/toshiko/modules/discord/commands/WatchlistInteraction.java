package me.anisekai.toshiko.modules.discord.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.modules.discord.Texts;
import me.anisekai.toshiko.modules.discord.annotations.InteractionBean;
import me.anisekai.toshiko.modules.discord.messages.responses.SimpleResponse;
import me.anisekai.toshiko.modules.discord.utils.PermissionUtils;
import me.anisekai.toshiko.services.data.WatchlistDataService;
import org.springframework.stereotype.Component;

@InteractionBean
@Component
public class WatchlistInteraction {

    private final WatchlistDataService service;

    public WatchlistInteraction(WatchlistDataService service) {

        this.service = service;
    }

    @Interact(
            name = "watchlist/refresh",
            description = Texts.WATCHLIST_REFRESH__DESCRIPTION
    )
    public SlashResponse runWatchlistRefresh(DiscordUser sender) {

        PermissionUtils.requirePrivileges(sender);
        this.service.refreshAll();
        return new SimpleResponse("Les listes de visionnage vont être actualisées sous peu.", false, false);
    }

}
