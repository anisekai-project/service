package me.anisekai.toshiko.modules.discord.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.modules.discord.annotations.InteractAt;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.enums.InteractionType;
import me.anisekai.toshiko.modules.discord.annotations.InteractionBean;
import me.anisekai.toshiko.modules.discord.messages.embeds.SeasonalSelectionEmbed;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.services.SeasonalSelectionService;
import me.anisekai.toshiko.modules.discord.utils.PermissionUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class SeasonalSelectionInteractions {

    private final SeasonalSelectionService service;
    private final AnimeService             animeService;

    public SeasonalSelectionInteractions(SeasonalSelectionService service, AnimeService animeService) {

        this.service      = service;
        this.animeService = animeService;
    }

    // <editor-fold desc="@ season/start ─ Commence un vote de simulcast pour la prochaine saison">
    @Interact(
            name = "season/start",
            description = "Commence un vote de simulcast pour la prochaine saison",
            options = {
                    @Option(
                            name = "name",
                            description = "Nom de la saison (ex: Hiver 2023)",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse startSeasonalSelection(DiscordUser user, @Param("name") String name) {

        PermissionUtils.requirePrivileges(user);
        SeasonalSelection ss = this.service.createSeasonalSelection(name);

        return new SeasonalSelectionEmbed(ss);
    }
    // </editor-fold>

    // <editor-fold desc="@ season/cast ─ Défini un choix de simulcast">
    @Interact(
            name = "season/cast",
            description = "Défini un choix de simulcast",
            options = {
                    @Option(
                            name = "seasonal",
                            description = "Selection saisonnière pour laquelle le simulcast sera choisi",
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "anime",
                            description = "Anime choisi pour la saison",
                            type = OptionType.INTEGER,
                            required = true
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public SlashResponse castSimulcastVote(DiscordUser user, @Param("seasonal") Long seasonalSelectionId, @Param("anime") Long animeId) {

        Anime             anime = this.animeService.getAnime(animeId);
        SeasonalSelection ss    = this.service.getSelection(seasonalSelectionId);

        return new SeasonalSelectionEmbed(this.service.castVote(ss, user, anime));
    }
    // </editor-fold>


    // <editor-fold desc="@ season/close ─ Termine un vote de simulcast">
    @Interact(
            name = "season/close",
            description = "Termine un vote de simulcast",
            options = {
                    @Option(
                            name = "seasonal",
                            description = "Selection saisonnière à terminer",
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public SlashResponse stopSeasonalSelection(DiscordUser user, @Param("seasonal") String seasonalSelectionId) {

        PermissionUtils.requirePrivileges(user);
        SeasonalSelection selection = this.service.getSelection(Long.parseLong(seasonalSelectionId));
        this.service.closeSeasonalSelection(selection, true);

        return new SeasonalSelectionEmbed(selection);
    }
    // </editor-fold>

}
