package me.anisekai.toshiko.modules.discord.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.IUser;
import me.anisekai.toshiko.modules.discord.Texts;
import me.anisekai.toshiko.modules.discord.annotations.InteractionBean;
import me.anisekai.toshiko.modules.discord.messages.embeds.AnimeEmbed;
import me.anisekai.toshiko.modules.discord.messages.responses.SimpleResponse;
import me.anisekai.toshiko.modules.discord.utils.PermissionUtils;
import me.anisekai.toshiko.services.data.AnimeDataService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class AnimeInteractions {

    private final AnimeDataService          service;
    private final RankingHandler            ranking;

    public AnimeInteractions(AnimeDataService service, RankingHandler ranking) {

        this.service   = service;
        this.ranking   = ranking;
    }

    // <editor-fold desc="@ anime/announce">
    @Interact(
            name = "anime/announce",
            description = Texts.ANIME_NOTIFY_ANNOUNCE__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            required = true,
                            description = Texts.ANIME_NOTIFY_ANNOUNCE__OPTION_ANIME,
                            type = OptionType.INTEGER,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse sendAnimeNotification(IUser discordUser, @Param("anime") long animeId) {

        PermissionUtils.requirePrivileges(discordUser);
        Anime anime = this.service.fetch(animeId);
        this.service.announce(anime);

        return new SimpleResponse("L'annonce de l'anime va être envoyée sous peu.", false, false);
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

        Anime      anime   = this.service.fetch(animeId);
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
    public SlashResponse runAnimeStatus(IUser discordUser, @Param("anime") long animeId, @Param("status") String statusName) {

        PermissionUtils.requirePrivileges(discordUser);
        AnimeStatus status = AnimeStatus.from(statusName);
        Anime       anime  = this.service.mod(animeId, proxy -> proxy.setStatus(status));

        return new SimpleResponse(
                String.format("Le statut de l'anime '%s' a bien été changé.", anime.getName()),
                false,
                false
        );
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
                            name = "total",
                            description = Texts.ANIME_PROGRESS__OPTION_AMOUNT,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse runAnimeProgress(
            IUser user,
            @Param("anime") long animeId,
            @Param("watched") long watched,
            @Param("total") Long total
    ) {

        PermissionUtils.requirePrivileges(user);
        Anime anime = this.service.mod(animeId, this.service.progression(watched, total));

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
    public SlashResponse runAnimeImport(DiscordUser user, @Param("json") String rawJson) {

        UpsertResult<Anime> result = this.service.runImport(user, new JSONObject(rawJson));

        if (result.isNew()) {
            return new SimpleResponse("L'anime a bien été créé.", false, false);
        } else {
            return new SimpleResponse("L'anime a bien été mis à jour.", false, false);
        }
    }
    // </editor-fold>
}
