package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.WatchlistUpdatedEvent;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.repositories.UserRepository;
import me.anisekai.toshiko.services.UserService;
import me.anisekai.toshiko.services.responses.SimpleResponse;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class UserInteractions {

    private final UserService service;
    private final ApplicationEventPublisher publisher;

    public UserInteractions(UserService service, ApplicationEventPublisher publisher) {

        this.service = service;
        this.publisher  = publisher;
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
    public SlashResponse changeUserIcon(User user, @Param("icon") String icon) {

        if (!this.service.swapEmoji(user, icon)) {
            return new SimpleResponse("Ton icône de vote reste inchangée.", false, true);
        }

        for (AnimeStatus status : AnimeStatus.getDisplayable()) {
            this.publisher.publishEvent(new WatchlistUpdatedEvent(this, status));
        }

        return new SimpleResponse("Ton icône de vote a été mise à jour.", false, false);
    }
    // </editor-fold>
}
