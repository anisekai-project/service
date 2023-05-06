package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.messages.embeds.SeasonalSelectionEmbed;
import me.anisekai.toshiko.messages.responses.SimpleResponse;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.services.SeasonalSelectionService;
import me.anisekai.toshiko.utils.PermissionUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "anime",
                            description = "Anime choisi pour la saison",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse castSimulcastVote(DiscordUser user, @Param("id") Long seasonalSelectionId, @Param("anime") Long animeId) {

        Anime             anime = this.animeService.getAnime(animeId);
        SeasonalSelection ss    = this.service.getSelection(seasonalSelectionId);

        // Check the vote eligibility of the user
        if (ss.getVoters().stream().noneMatch(voter -> voter.getUser().equals(user))) {
            return new SimpleResponse("Tu n'es pas autorisé à choisir un simulcast.", false, true);
        }

        // Check if anime is already voted.
        Optional<SeasonalVote> optionalVote = ss.getVotes()
                                                .stream()
                                                .filter(sv -> sv.getAnime().equals(anime))
                                                .findFirst();

        if (optionalVote.isPresent()) {
            // Sanity checks
            SeasonalVote vote = optionalVote.get();

            if (!vote.getUser().equals(user)) {
                return new SimpleResponse("Cet anime est déjà choisi.", false, true);
            }

            // TODO: Remove vote

            return new SimpleResponse("Ton choix a bien été retiré.", false, true);
        }


        // TODO
        return new SimpleResponse("La commande n'est pas encore prête.", false, true);
    }
    // </editor-fold>
}
