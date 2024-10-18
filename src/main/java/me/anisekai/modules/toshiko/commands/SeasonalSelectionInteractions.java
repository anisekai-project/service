package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.chiya.interfaces.IUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.enums.InteractionType;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalSelection;
import me.anisekai.modules.shizue.services.ShizueService;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.shizue.services.data.SeasonalSelectionDataService;
import me.anisekai.modules.shizue.services.data.SeasonalVoteDataService;
import me.anisekai.modules.toshiko.Texts;
import me.anisekai.modules.toshiko.annotations.InteractAt;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.embeds.SeasonalSelectionEmbed;
import me.anisekai.modules.toshiko.utils.PermissionUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class SeasonalSelectionInteractions {

    private final SeasonalSelectionDataService service;
    private final SeasonalVoteDataService voteService;
    private final ShizueService           shizueService;
    private final AnimeDataService        animeService;

    public SeasonalSelectionInteractions(SeasonalSelectionDataService service, SeasonalVoteDataService voteService, ShizueService shizueService, AnimeDataService animeService) {

        this.service       = service;
        this.voteService   = voteService;
        this.shizueService = shizueService;
        this.animeService  = animeService;
    }


    // <editor-fold desc="@ season/start [name: string]">
    @Interact(
            name = "season/start",
            description = Texts.SEASON_START__DESCRIPTION,
            options = {
                    @Option(
                            name = "name",
                            description = Texts.SEASON_START__OPTION_NAME,
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse runSeasonStart(IUser user, @Param("name") String name) {

        PermissionUtils.requirePrivileges(user);
        ISeasonalSelection selection = this.shizueService.createNewSelection(name);
        return new SeasonalSelectionEmbed(selection);
    }
    // </editor-fold>

    // <editor-fold desc="@ season/cast [seasonal: integer, anime: integer]">
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
    public SlashResponse runSeasonCast(DiscordUser user, @Param("seasonal") Long seasonalSelectionId, @Param("anime") Long animeId) {

        Anime             anime     = this.animeService.fetch(animeId);
        SeasonalSelection selection = this.service.fetch(seasonalSelectionId);

        this.voteService.toggleVote(selection, user, anime);

        return new SeasonalSelectionEmbed(this.service.fetch(seasonalSelectionId));
    }
    // </editor-fold>


    // <editor-fold desc="@ season/close [seasonal: integer]">
    @Interact(
            name = "season/close",
            description = "Termine un vote de simulcast",
            options = {
                    @Option(
                            name = "seasonal",
                            description = "Selection saisonnière à terminer",
                            type = OptionType.INTEGER,
                            required = true
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public SlashResponse runSeasonClose(IUser user, @Param("seasonal") Long seasonalSelectionId) {

        PermissionUtils.requirePrivileges(user);
        SeasonalSelection selection = this.service.close(seasonalSelectionId);
        return new SeasonalSelectionEmbed(selection);
    }
    // </editor-fold>

}
