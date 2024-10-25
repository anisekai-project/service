package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.freya.entities.detached.NyaaRssEntry;
import me.anisekai.modules.freya.entities.detached.disk.DiskAnime;
import me.anisekai.modules.freya.entities.detached.disk.DiskGroup;
import me.anisekai.modules.freya.services.DiskService;
import me.anisekai.modules.freya.services.RSSService;
import me.anisekai.modules.freya.services.ToshikoFileSystem;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@InteractionBean
@Component
public class AdministrativeInteraction {

    private final AnimeDataService  animeService;
    private final ToshikoFileSystem fsService;
    private final RSSService        rssService;


    public AdministrativeInteraction(AnimeDataService animeService, ToshikoFileSystem fsService, RSSService rssService) {

        this.animeService = animeService;
        this.fsService    = fsService;
        this.rssService   = rssService;
    }

    private SlashResponse notEnoughPermission() {

        return new SimpleResponse("Tu n'as pas la permission de faire ça.", false, true);
    }

    // <editor-fold desc="@ admin/tasks/check-automation">
    @Interact(
            name = "admin/tasks/check-automation",
            description = "\uD83D\uDD12 Execute la tâche d'automatisation d'import",
            target = SlashTarget.ALL,
            defer = true
    )
    public SlashResponse adminCheckAutomation(DiscordUser user) {

        if (!user.isAdmin()) return this.notEnoughPermission();
        int count = this.fsService.checkForAutomation();
        return new SimpleResponse(String.format("%s anime vont être importés.", count), false, true);
    }
    // </editor-fold>

    // <editor-fold desc="@ admin/tasks/check-rss [rss: string]">
    @Interact(
            name = "admin/tasks/check-rss",
            description = "\uD83D\uDD12 Execute la tâche d'automatisation de téléchargement via RSS",
            target = SlashTarget.ALL,
            defer = true,
            options = {
                    @Option(
                            name = "rss",
                            description = "Lien RSS qui sera utilisé à la place de celui par défaut",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse adminCheckRSS(DiscordUser user, @Param("rss") String rss) {

        if (!user.isAdmin()) return this.notEnoughPermission();

        // Custom override to add more flexibility
        if (Objects.nonNull(rss)) this.rssService.setUrlOverride(rss);

        List<NyaaRssEntry> result = this.rssService.execute();

        if (result.isEmpty()) {
            return new SimpleResponse("Aucun épisode à télécharger n'a été détecté.", false, false);
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(String.format("%s épisodes vont être téléchargés.\n", result.size()));

        result.stream().limit(24)
              .forEach(entry -> builder.addField(entry.getTitle(), entry.getLink(), false));

        if (result.size() > 24) {
            builder.addField(String.format("and %s more", result.size() - 24), "...", false);
        }

        return new SimpleResponse(builder, false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ admin/anime/link [anime: integer, diskgroup: string, diskanime: string]">
    @Interact(
            name = "admin/anime/link",
            description = "\uD83D\uDD12 Associe un anime à un dossier existant sur le disque",
            target = SlashTarget.ALL,
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = "L'anime à associer",
                            type = OptionType.INTEGER,
                            autoComplete = true
                    ),
                    @Option(
                            name = "diskanime",
                            description = "Groupe de dossier de l'anime (généralement le nom de l'anime)",
                            type = OptionType.STRING,
                            autoComplete = true
                    ),
                    @Option(
                            name = "diskgroup",
                            description = "Dossier de l'anime (généralement le nom de la saison)",
                            type = OptionType.STRING,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse adminAnimeLink(DiscordUser user, @Param("anime") long animeId, @Param("diskanime") String diskAnimeUuid, @Param("diskgroup") String diskGroupUuid) {

        if (!user.isAdmin()) return this.notEnoughPermission();
        UUID diskAnimeIdentifier = UUID.fromString(diskAnimeUuid);
        UUID diskGroupIdentifier = UUID.fromString(diskGroupUuid);

        // Find DiskAnime
        Optional<DiskAnime> optionalDiskAnime = this.fsService
                .getDiskService()
                .getDatabase()
                .stream()
                .filter(diskAnime -> diskAnime.getUuid().equals(diskAnimeIdentifier))
                .findFirst();

        if (optionalDiskAnime.isEmpty()) {
            return new SimpleResponse("Impossible de créer l'association demandée. (Err: DiskAnime)", false, false);
        }

        DiskAnime diskAnime = optionalDiskAnime.get();

        Optional<DiskGroup> optionalDiskGroup = diskAnime
                .getGroups()
                .stream()
                .filter(diskGroup -> diskGroup.getUuid().equals(diskGroupIdentifier))
                .findAny();

        if (optionalDiskGroup.isEmpty()) {
            return new SimpleResponse("Impossible de créer l'association demandée. (Err: DiskGroup)", false, false);
        }

        DiskGroup diskGroup = optionalDiskGroup.get();

        String path  = String.format("%s/%s", diskAnime.getFile().getName(), diskGroup.getFile().getName());
        Anime  saved = this.animeService.mod(animeId, anime -> anime.setDiskPath(path));

        return new SimpleResponse(String.format(
                "Le chemin disque de l'anime **%s** a été défini à `%s`.",
                saved.getName(),
                saved.getDiskPath()
        ), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ admin/anime/rss-match [anime: integer, match: string]">
    @Interact(
            name = "admin/anime/rss-match",
            description = "\uD83D\uDD12 Défini la chaîne de caractère qui sera utiliser pour la correspondance RSS",
            target = SlashTarget.ALL,
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = "L'anime à modifier",
                            required = true,
                            type = OptionType.INTEGER,
                            autoComplete = true
                    ),
                    @Option(
                            name = "match",
                            description = "Chaîne de caractère",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse adminAnimeRssMatch(DiscordUser user, @Param("anime") long animeId, @Param("match") String match) {

        if (match == null || match.isEmpty()) {
            Anime saved = this.animeService.mod(animeId, anime -> anime.setDiskPath(null));
            return new SimpleResponse(String.format(
                    "Le téléchargement automatique pour l'anime **%s** a été désactivé.",
                    saved.getName()
            ), false, false);
        } else {
            Anime saved = this.animeService.mod(animeId, anime -> anime.setDiskPath(match));
            return new SimpleResponse(String.format(
                    "Le texte de correspondance pour l'anime **%s** a été défini sur `%s`.",
                    saved.getName(),
                    match
            ), false, false);
        }
    }
    // </editor-fold>

}
