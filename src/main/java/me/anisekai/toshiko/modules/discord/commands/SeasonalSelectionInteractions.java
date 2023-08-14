package me.anisekai.toshiko.modules.discord.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.enums.InteractionType;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.ISeasonalSelection;
import me.anisekai.toshiko.interfaces.entities.IUser;
import me.anisekai.toshiko.modules.discord.Texts;
import me.anisekai.toshiko.modules.discord.annotations.InteractAt;
import me.anisekai.toshiko.modules.discord.annotations.InteractionBean;
import me.anisekai.toshiko.modules.discord.messages.embeds.SeasonalSelectionEmbed;
import me.anisekai.toshiko.modules.discord.utils.PermissionUtils;
import me.anisekai.toshiko.services.ToshikoService;
import me.anisekai.toshiko.services.data.AnimeDataService;
import me.anisekai.toshiko.services.data.SeasonalSelectionDataService;
import me.anisekai.toshiko.services.data.SeasonalVoteDataService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class SeasonalSelectionInteractions {

    private final SeasonalSelectionDataService service;
    private final SeasonalVoteDataService      voteService;
    private final ToshikoService               toshikoService;
    private final AnimeDataService             animeService;

    public SeasonalSelectionInteractions(SeasonalSelectionDataService service, SeasonalVoteDataService voteService, ToshikoService toshikoService, AnimeDataService animeService) {

        this.service        = service;
        this.voteService    = voteService;
        this.toshikoService = toshikoService;
        this.animeService   = animeService;
    }


    // <editor-fold desc="@ season/start">
    @Interact(
            name = "season/start",
            description = Texts.SEASON_START__DESCRIPTION,
            options = {
                    @Option(
                            name = "name",
                            description = Texts.SEASON_START__OPTION_NAME,
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse runSeasonStart(IUser user, @Param("name") String name) {

        PermissionUtils.requirePrivileges(user);
        ISeasonalSelection selection = this.toshikoService.createNewSelection(name);
        return new SeasonalSelectionEmbed(selection);
    }
    // </editor-fold>

    // <editor-fold desc="@ season/cast">
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


    // <editor-fold desc="@ season/close">
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
