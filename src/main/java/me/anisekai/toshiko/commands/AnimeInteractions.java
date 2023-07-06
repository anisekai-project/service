package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.data.anime.AnimeImportResult;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.anime.AnimeCreatedEvent;
import me.anisekai.toshiko.events.anime.AnimeUpdatedEvent;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.messages.embeds.AnimeEmbed;
import me.anisekai.toshiko.messages.embeds.InterestResponse;
import me.anisekai.toshiko.messages.responses.SimpleResponse;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.services.InterestService;
import me.anisekai.toshiko.utils.PermissionUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component
@InteractionBean
public class AnimeInteractions {

    private final AnimeService              animeService;
    private final InterestService           interestService;
    private final RankingHandler            ranking;
    private final ApplicationEventPublisher publisher;

    public AnimeInteractions(AnimeService animeService, InterestService interestService, RankingHandler ranking, ApplicationEventPublisher publisher) {

        this.animeService    = animeService;
        this.interestService = interestService;
        this.ranking         = ranking;
        this.publisher       = publisher;
    }

    // <editor-fold desc="@ anime/announce">
    @Interact(
            name = "anime/announce",
            description = Texts.ANIME_NOTIFY_ANNOUNCE__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.ANIME_NOTIFY_ANNOUNCE__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse sendAnimeNotification(DiscordUser discordUser, @Param("anime") Long animeId) {

        PermissionUtils.requirePrivileges(discordUser);
        Collection<Anime> animes = new ArrayList<>();

        if (animeId != null) {
            animes.add(this.animeService.getAnime(animeId));
        } else {
            animes.addAll(this.animeService.getRepository().findAll());
        }

        for (Anime anime : animes) {
            if (anime.getAnnounceMessage() == null) {
                this.publisher.publishEvent(new AnimeCreatedEvent(this, anime));
            } else {
                this.publisher.publishEvent(new AnimeUpdatedEvent(this, anime));
            }
        }

        String text = animes.size() == 1 ? "%s annonce va être envoyée." : "%s annonces vont être envoyées.";
        return new SimpleResponse(String.format(text, animes.size()), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/about">
    @Interact(
            name = "anime/about",
            description = Texts.ANIME_ABOUT__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.ANIME_ABOUT__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse showAnimeDetails(@Param("anime") long animeId) {

        Anime      anime   = this.animeService.getAnime(animeId);
        AnimeEmbed message = new AnimeEmbed(anime, this.ranking.getAnimeScore(anime));
        message.setShowButtons(true);
        return message;
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/status">
    @Interact(
            name = "anime/status",
            description = Texts.ANIME_STATUS__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.ANIME_STATUS__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "status",
                            description = Texts.ANIME_STATUS__OPTION_STATUS,
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse changeAnimeStatus(DiscordUser discordUser, @Param("anime") long animeId, @Param("status") String statusName) {

        PermissionUtils.requirePrivileges(discordUser);

        AnimeStatus status = AnimeStatus.from(statusName);
        Anime       anime  = this.animeService.setStatus(this.animeService.getAnime(animeId), status);

        return new SimpleResponse(
                String.format("Le statut de l'anime '%s' a bien été changé.", anime.getName()),
                false,
                false
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/interest">
    @Interact(
            name = "anime/interest",
            description = Texts.ANIME_INTEREST__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.ANIME_INTEREST__OPTION_NAME,
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "interest",
                            description = Texts.ANIME_INTEREST__OPTION_LEVEL,
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse interestedByAnime(
            Interaction interaction,
            DiscordUser discordUser,
            User user,
            @Param("anime") long animeId,
            @Param("interest") String interestName
    ) {

        if (discordUser.getEmote() == null) {
            return new SimpleResponse(
                    "Avant de pouvoir vote pour un anime, tu dois définir ton icône de vote. (`/profile`)",
                    false,
                    true
            );
        }

        Anime anime = this.animeService.getAnime(animeId);

        InterestLevel level = InterestLevel.from(interestName);

        Optional<Interest> optionalInterest = this.interestService.setInterestLevel(anime, discordUser, level);

        if (optionalInterest.isEmpty()) {
            return new SimpleResponse(
                    "Ton niveau d'intérêt reste inchangé.",
                    false,
                    interaction instanceof ButtonInteraction
            );
        }

        return new InterestResponse(optionalInterest.get(), interaction instanceof SlashCommandInteraction);
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/progress">
    @Interact(
            name = "anime/progress",
            description = Texts.ANIME_PROGRESS__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.ANIME_PROGRESS__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "watched",
                            description = Texts.ANIME_PROGRESS__OPTION_WATCHED,
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = Texts.ANIME_PROGRESS__OPTION_AMOUNT,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse changeAnimeProgress(
            DiscordUser user,
            @Param("anime") long animeId,
            @Param("watched") long watched,
            @Param("amount") Long amount
    ) {

        PermissionUtils.requirePrivileges(user);

        Anime anime = this.animeService.getAnime(animeId);

        if (amount != null) {
            anime = this.animeService.setTotal(anime, amount);
        }

        anime = this.animeService.setProgression(anime, watched);

        if (anime.getStatus() == AnimeStatus.WATCHED) {
            return new SimpleResponse(
                    "La progression a été sauvegardée et l'anime marqué comme terminé.",
                    false,
                    false
            );
        } else {
            return new SimpleResponse("La progression a été sauvegardée.", false, false);
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/import">
    @Interact(
            name = "anime/import",
            description = Texts.ANIME_IMPORT__DESCRIPTION,
            options = {
                    @Option(
                            name = "json",
                            required = true,
                            type = OptionType.STRING,
                            description = Texts.ANIME_IMPORT__OPTION_JSON
                    )
            },
            defer = true
    )
    public SlashResponse importFromJson(DiscordUser user, @Param("json") String rawJson) {

        AnimeImportResult air = this.animeService.importAnime(user, new JSONObject(rawJson));

        return switch (air.state()) {
            case CREATED -> new SimpleResponse("L'anime a bien été créé.", false, false);
            case UPDATED -> new SimpleResponse("L'anime a bien été mis à jour.", false, false);
        };
    }
    // </editor-fold>
}
