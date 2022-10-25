package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class UserInteractions {

    private final ToshikoService toshikoService;

    public UserInteractions(ToshikoService toshikoService) {

        this.toshikoService = toshikoService;
    }

    // <editor-fold desc="@ user/icon/set ─ Change your icon">
    @Interact(
            name = "user/icon/set",
            description = Texts.USER_ICON__DESCRIPTION,
            options = {
                    @Option(
                            name = "icon",
                            description = Texts.USER_ICON__OPTION_ICON,
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse changeUserIcon(DiscordUser discordUser, User user, @Param("icon") String icon) {

        if (!this.toshikoService.setUserEmoji(user, icon)) {
            return new SimpleResponse("Ton icône de vote reste inchangée.", false, true);
        }

        if (!discordUser.isBanned()) {
            this.toshikoService.queueUpdateAll(false);
        }

        return new SimpleResponse("Ton icône de vote a été mise à jour.", false, false);
    }
    // </editor-fold>

}
