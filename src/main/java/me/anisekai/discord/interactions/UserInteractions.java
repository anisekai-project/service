package me.anisekai.discord.interactions;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.discord.annotations.InteractAt;
import me.anisekai.discord.annotations.InteractionBean;
import me.anisekai.discord.responses.DiscordResponse;
import me.anisekai.discord.responses.messages.AnimeCardMessage;
import me.anisekai.discord.responses.messages.ProfileMessage;
import me.anisekai.discord.responses.messages.SelectionMessage;
import me.anisekai.discord.utils.InteractionType;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Interest;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.services.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@InteractionBean
public class UserInteractions {

    private final AnimeService       animeService;
    private final DiscordUserService userService;
    private final InterestService    interestService;
    private final SelectionService   selectionService;
    private final VoterService       voterService;

    public UserInteractions(AnimeService animeService, DiscordUserService userService, InterestService interestService, SelectionService selectionService, VoterService voterService) {

        this.animeService     = animeService;
        this.userService      = userService;
        this.interestService  = interestService;
        this.selectionService = selectionService;
        this.voterService     = voterService;
    }

    // <editor-fold desc="@ card ─ Displays an anime card message [anime: integer]">
    @Interact(
            name = "anime-card",
            description = "Permet de visionner la fiche d'un anime.",
            options = {
                    @Option(
                            autoComplete = true,
                            description = "Anime pour lequel la fiche sera envoyée.",
                            name = "anime",
                            required = true,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse viewAnime(@Param("anime") long animeId) {

        Anime          anime     = this.animeService.fetch(animeId);
        List<Interest> interests = this.interestService.getInterests(anime);
        return new AnimeCardMessage(anime, interests);
    }
    // </editor-fold>

    // <editor-fold desc="@ emote ─ Change the vote emote [icon: string]">
    @Interact(
            name = "emote",
            description = "Change l'emote de vote.",
            options = {
                    @Option(
                            name = "emote",
                            description = "Emote de vote",
                            type = OptionType.STRING,
                            required = true
                    ),
            }
    )
    public SlashResponse changeEmote(IDiscordUser user, @Param("emote") String emote) {

        if (!this.userService.canUseEmote(user, emote)) {
            return DiscordResponse.error("L'emote choisie est déjà utilisée.");
        }

        this.userService.useEmote(user, emote);
        return DiscordResponse.success("L'emote de vote a été mise à jour.");
    }
    // </editor-fold>

    // <editor-fold desc="@ import ─ Import an anime into database. [json: string]">
    @Interact(
            name = "import",
            description = "Importe un anime dans la base de donnée du bot.",
            options = {
                    @Option(
                            name = "json",
                            description = "JSON obtenu depuis l'extension navigateur.",
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse animeImport(DiscordUser user, @Param("json") String json) {

        UpsertResult<Anime> result = this.animeService.importAnime(user, new JSONObject(json));

        if (result.isNew()) {
            return DiscordResponse.success("L'anime **%s** a été ajouté au bot.", result.result().getTitle());
        } else {
            return DiscordResponse.info("L'anime **%s** a été mis à jour.", result.result().getTitle());
        }
    }
    // </editor-fold>

    // <editor-fold desc="% interest ─ Change the interest level on an anime. [anime: integer, interest: integer]">
    @Interact(
            name = "interest",
            description = "Change l'intérêt porté à un anime.",
            options = {
                    @Option(
                            autoComplete = true,
                            description = "Anime pour lequel la fiche sera envoyée.",
                            name = "anime",
                            required = true,
                            type = OptionType.INTEGER
                    ),
                    @Option(
                            // TODO Add to autocomplete
                            name = "interest",
                            description = "Le niveau d'intérêt",
                            required = true,
                            type = OptionType.INTEGER,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse animeInterest(DiscordUser user, @Param("anime") long animeId, @Param("interest") long interestLevel) {

        Anime anime = this.animeService.fetch(animeId);

        if (interestLevel < -2 || interestLevel > 2) {
            return DiscordResponse.error("La valeur d'intérêt doit être comprise entre -2 (inclus) et 2 (inclus)");
        }

        this.interestService.setInterest(user, anime, interestLevel);
        return DiscordResponse.success("Le niveau d'intérêt a bien été mis à jour.");
    }
    // </editor-fold>

    // <editor-fold desc="@ profile ─ Display a user profile. [user: Member]">
    @Interact(
            name = "profile",
            description = "Afficher le profil utilisateur.",
            options = {
                    @Option(
                            name = "user",
                            description = "Utilisateur pour lequel sera affiché le profil. (Par défaut: vous)",
                            type = OptionType.USER
                    )
            }
    )
    public SlashResponse profileView(User sender, @Param("user") Member member) {

        User           effectiveUser        = member == null ? sender : member.getUser();
        DiscordUser    effectiveDiscordUser = this.userService.of(effectiveUser);
        List<Anime>    animes               = this.animeService.getAnimesAddedByUser(effectiveDiscordUser);
        List<Interest> interests            = this.interestService.getInterests(effectiveDiscordUser);

        return new ProfileMessage(effectiveUser, effectiveDiscordUser, animes, interests);
    }
    // </editor-fold>

    // <editor-fold desc="█ vote ─ Cast a vote on a selection [selection: integer, anime: integer]">
    @Interact(
            name = "vote",
            description = "Vote pour un anime d'une selection.",
            options = {
                    @Option(
                            name = "selection",
                            description = "",
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "anime",
                            description = "",
                            type = OptionType.INTEGER,
                            required = true
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public ButtonResponse castSelectionVote(DiscordUser user, @Param("selection") long selectionId, @Param("anime") long animeId) {

        Selection selection = this.selectionService.fetch(selectionId);
        Anime     anime     = this.animeService.fetch(animeId);

        this.voterService.castVote(selection, user, anime);
        return new SelectionMessage(selection, this.voterService.getVoters(selection));
    }
    // </editor-fold>

}
