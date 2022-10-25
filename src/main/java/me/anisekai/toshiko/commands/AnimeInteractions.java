package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.embeds.AnimeEmbed;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.providers.OfflineProvider;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class AnimeInteractions {

    private final ToshikoService toshikoService;

    public AnimeInteractions(ToshikoService toshikoService) {

        this.toshikoService = toshikoService;
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
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse sendAnimeNotification(DiscordUser discordUser, @Param("anime") long animeId) {

        if (!discordUser.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça.", false, false);
        }

        Anime anime = this.toshikoService.findAnime(animeId);
        this.toshikoService.createAnimeAnnounce(anime);
        return new SimpleResponse("La notification sera envoyée sous peu.", false, false);
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

        Anime      anime        = this.toshikoService.findAnime(animeId);
        AnimeEmbed sheetMessage = new AnimeEmbed(anime);
        sheetMessage.setShowButtons(true);
        return sheetMessage;
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
                    ),
                    @Option(
                            name = "episode",
                            description = Texts.ANIME_STATUS__OPTION_WATCHED,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse changeAnimeStatus(DiscordUser discordUser, @Param("anime") long animeId, @Param("status") String statusName) {

        if (!discordUser.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça.", false, false);
        }

        AnimeStatus status = AnimeStatus.from(statusName);
        Anime       anime  = this.toshikoService.setAnimeStatus(animeId, status);
        return new SimpleResponse(String.format("Le statut de l'anime '%s' a bien été changé.", anime.getName()), false, false);
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
            },
            hideAsButton = true
    )
    public SlashResponse interestedByAnime(
            Interaction interaction,
            DiscordUser discordUser,
            User user,
            @Param("anime") long animeId,
            @Param("interest") String interestName
    ) {

        if (discordUser.getEmote() == null) {
            return new SimpleResponse("Avant de pouvoir vote pour un anime, tu dois définir ton icône de vote. (`/user icon set`)", false, true);
        }

        InterestLevel level    = InterestLevel.from(interestName);
        Interest      interest = this.toshikoService.setInterestLevel(animeId, user, level);


        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Ton niveau d'interêt pour cet anime a bien été mis à jour.");

        if (discordUser.isBanned()) {
            builder.appendDescription("\n");
            builder.appendDescription("Mais suite à une décision *administrative*, tes votes ne sont plus comptabilisés.");
        }

        builder.addField("Anime", interest.getAnime().getName(), false);
        builder.addField("Niveau d'interêt", interest.getLevel().getDisplayText(), false);

        return new SimpleResponse(builder, false, interaction instanceof ButtonInteraction);
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

        if (!user.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, false);
        }

        Anime anime;
        if (amount != null) {
            anime = this.toshikoService.setAnimeProgression(animeId, watched, amount);
        } else {
            anime = this.toshikoService.setAnimeProgression(animeId, watched);
        }

        if (anime.getStatus() == AnimeStatus.WATCHED) {
            return new SimpleResponse("La progression a été sauvegardée et l'anime marqué comme terminé.", false, false);
        } else {
            return new SimpleResponse("La progression a été sauvegardée.", false, false);
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/refresh">
    @Interact(
            name = "anime/refresh",
            description = Texts.ANIME_REFRESH__DESCRIPTION,
            options = {
                    @Option(
                            name = "target",
                            description = "Ce qui doit être actualisé",
                            type = OptionType.STRING,
                            required = true,
                            choices = {
                                    @Choice(
                                            id = "watchlist",
                                            display = "Watchlist"
                                    ),
                                    @Choice(
                                            id = "announce",
                                            display = "Annonces"
                                    )
                            }
                    ),
                    @Option(
                            name = "force",
                            description = "Si l'actualisation doit être forcée (utile sur la watchlist seulement)",
                            type = OptionType.BOOLEAN
                    )
            }
    )
    public SlashResponse refreshWatchlist(DiscordUser user, @Param("target") String target, @Param("force") Boolean force) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, false);
        }

        if (target.equals("watchlist")) {
            this.toshikoService.queueUpdateAll(force);
            return new SimpleResponse("Les listes seront actualisées sous peu.", false, false);
        } else if (target.equals("announce")) {
            this.toshikoService.getAnimeRepository().findAll().stream()
                               .filter(anime -> anime.getAnnounceMessage() != null)
                               .forEach(this.toshikoService::refreshAnimeAnnounce);

            return new SimpleResponse("Les annonces seront actualisées sous peu.", false, false);
        } else {
            return new SimpleResponse("Hmmm, quelque chose s'est mal passé.", false, false);
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/add">
    @Interact(
            name = "anime/add",
            description = Texts.ANIME_ADD__DESCRIPTION,
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "name",
                            description = Texts.ANIME_ADD__OPTION_NAME,
                            required = true,
                            type = OptionType.STRING
                    ),
                    @Option(
                            name = "link",
                            description = Texts.ANIME_ADD__OPTION_LINK,
                            required = true,
                            type = OptionType.STRING
                    ),
                    @Option(
                            name = "status",
                            description = Texts.ANIME_ADD__OPTION_STATUS,
                            required = true,
                            type = OptionType.STRING,
                            autoComplete = true
                    ),
                    @Option(
                            name = "episode",
                            description = Texts.ANIME_ADD__OPTION_EPISODE,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse addAnime(DiscordUser discordUser, User user,
            @Param("name") String name,
            @Param("link") String link, @Param("status") String status, @Param("episode") Long episode) {

        if (!AnimeProvider.isSupported(link)) {
            return new SimpleResponse("Désolé, mais ce n'est pas un lien Nautiljon...", false, true);
        }

        if (discordUser.isBanned()) {
            return new SimpleResponse("Désolé, mais suite à une décision *administrative*, tu ne peux plus ajouter d'anime.", false, false);
        }

        AnimeStatus   animeStatus = AnimeStatus.from(status);
        AnimeProvider provider    = new OfflineProvider(name, link, animeStatus, episode);
        Anime         anime       = this.toshikoService.createAnime(user, provider, animeStatus);
        return new SimpleResponse("L'anime %s a bien été ajouté !".formatted(anime.getName()), false, false);
    }
    // </editor-fold>
}
