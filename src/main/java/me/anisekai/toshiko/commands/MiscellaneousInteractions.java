package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class MiscellaneousInteractions {

    private final ToshikoService service;

    public MiscellaneousInteractions(ToshikoService service) {

        this.service = service;
    }

    // <editor-fold desc="@ refresh">
    @Interact(
            name = "refresh",
            description = Texts.REFRESH__DESCRIPTION,
            options = {
                    @Option(
                            name = "target",
                            description = Texts.REFRESH__OPTION_TARGET,
                            type = OptionType.STRING,
                            required = true,
                            choices = {
                                    @Choice(
                                            id = "watchlist",
                                            display = Texts.REFRESH__OPTION_TARGET__CHOICE_WATCHLIST
                                    ),
                                    @Choice(
                                            id = "announce",
                                            display = Texts.REFRESH__OPTION_TARGET__CHOICE_ANNOUNCE
                                    ),
                                    @Choice(
                                            id = "schedule",
                                            display = Texts.REFRESH__OPTION_TARGET__CHOICE_SCHEDULE
                                    )
                            }
                    ),
                    @Option(
                            name = "force",
                            description = Texts.REFRESH__OPTION_FORCE,
                            type = OptionType.BOOLEAN
                    )
            }
    )
    public SlashResponse refreshWatchlist(DiscordUser user, @Param("target") String target, @Param("force") Boolean force) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, false);
        }

        switch (target) {
            case "watchlist" -> {
                this.service.queueUpdateAll(force);
                return new SimpleResponse("Les listes seront actualisées sous peu.", false, false);
            }
            case "announce" -> {
                this.service.getAnimeRepository().findAll().stream()
                                   .filter(anime -> anime.getAnnounceMessage() != null)
                                   .forEach(this.service::refreshAnimeAnnounce);
                return new SimpleResponse("Les annonces seront actualisées sous peu.", false, false);
            }
            case "schedule" -> {
                this.service.refreshSchedule();
                return new SimpleResponse("Les évènements seront actualisés sous peu.", false, false);
            }
            default -> {
                return new SimpleResponse("Hmmm, quelque chose s'est mal passé.", false, false);
            }
        }
    }
    // </editor-fold>

}
