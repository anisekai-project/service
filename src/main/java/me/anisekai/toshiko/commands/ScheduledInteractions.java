package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.ButtonResponse;
import me.anisekai.toshiko.annotations.InteractAt;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.ScheduledEvent;
import me.anisekai.toshiko.enums.InteractionType;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.embeds.ScheduledEventEmbed;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@InteractionBean
public class ScheduledInteractions {

    private final ToshikoService toshikoService;

    public ScheduledInteractions(ToshikoService toshikoService) {

        this.toshikoService = toshikoService;
    }

    // <editor-fold desc="@ schedule/cancel ─ Cancel the given scheduled event">
    @Interact(
            name = "schedule/cancel",
            description = "Cancel the given scheduled event",
            options = {
                    @Option(
                            name = "id",
                            description = "",
                            required = true,
                            type = OptionType.INTEGER
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public ButtonResponse cancelScheduledEvent(User user, @Param("id") Long id) {

        DiscordUser discordUser = this.toshikoService.findUser(user);

        if (!discordUser.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, true);
        }

        Optional<ScheduledEvent> optionalEvent = this.toshikoService.cancelEvent(id.intValue());

        if (optionalEvent.isPresent()) {
            return new ScheduledEventEmbed(optionalEvent.get());
        } else {
            return new SimpleResponse("Impossible de trouver l'évènement.", false, true);
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/finish ─ Close the given scheduled event">
    @Interact(
            name = "schedule/finish",
            description = "Close the given scheduled event",
            options = {
                    @Option(
                            name = "id",
                            description = "",
                            required = true,
                            type = OptionType.INTEGER
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public ButtonResponse finishScheduledEvent(User user, @Param("id") Long id) {

        DiscordUser discordUser = this.toshikoService.findUser(user);

        if (!discordUser.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, true);
        }

        Optional<ScheduledEvent> optionalEvent = this.toshikoService.finishEvent(id.intValue());

        if (optionalEvent.isPresent()) {
            return new ScheduledEventEmbed(optionalEvent.get());
        } else {
            return new SimpleResponse("Impossible de trouver l'évènement.", false, true);
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ schedule/start ─ Close the given scheduled event">
    @Interact(
            name = "schedule/start",
            description = "Close the given scheduled event",
            options = {
                    @Option(
                            name = "id",
                            description = "",
                            required = true,
                            type = OptionType.INTEGER
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public ButtonResponse startScheduledEvent(User user, @Param("id") Long id) {

        DiscordUser discordUser = this.toshikoService.findUser(user);

        if (!discordUser.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, true);
        }

        Optional<ScheduledEvent> optionalEvent = this.toshikoService.startEvent(id.intValue());

        if (optionalEvent.isPresent()) {
            return new ScheduledEventEmbed(optionalEvent.get());
        } else {
            return new SimpleResponse("Impossible de trouver l'évènement.", false, true);
        }
    }
    // </editor-fold>

}
