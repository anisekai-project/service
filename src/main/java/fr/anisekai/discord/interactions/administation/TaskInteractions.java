package fr.anisekai.discord.interactions.administation;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import fr.anisekai.discord.annotations.InteractionBean;
import fr.anisekai.discord.exceptions.RequireAdministratorException;
import fr.anisekai.discord.responses.DiscordResponse;
import fr.anisekai.discord.tasks.watchlist.create.WatchlistCreateFactory;
import fr.anisekai.discord.tasks.watchlist.update.WatchlistUpdateFactory;
import fr.anisekai.library.Library;
import fr.anisekai.library.tasks.factories.MediaImportFactory;
import fr.anisekai.library.tasks.factories.TorrentRetentionControlFactory;
import fr.anisekai.library.tasks.factories.TorrentSourcingFactory;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.services.EpisodeService;
import fr.anisekai.server.services.SettingService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.utils.DiscordUtils;
import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
@InteractionBean
public class TaskInteractions {

    private final Library        library;
    private final AnimeService   animeService;
    private final EpisodeService episodeService;
    private final TaskService    service;
    private final SettingService settingService;

    public TaskInteractions(Library library, AnimeService animeService, EpisodeService episodeService, TaskService service, SettingService settingService) {

        this.library        = library;
        this.animeService   = animeService;
        this.episodeService = episodeService;
        this.service        = service;
        this.settingService = settingService;
    }

    private static void requireAdministrator(UserEntity user) {

        if (!user.isAdministrator()) {
            throw new RequireAdministratorException();
        }
    }

    // <editor-fold desc="@ task/check-downloads ─ Check for new episode to automatically download">
    @Interact(
            name = "task/check-downloads",
            description = "\uD83D\uDD12 — Vérifie si des épisodes peuvent être téléchargés.",
            options = {
                    @Option(
                            name = "url",
                            description = "URL source à utiliser au lieu de l'url configurée",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse checkDownloads(UserEntity user, @Param("url") String url) {

        requireAdministrator(user);

        Optional<String> optionalUserSource    = Optional.ofNullable(url);
        Optional<String> optionalDefaultSource = this.settingService.getDownloadSource();

        if (optionalUserSource.isPresent()) {
            this.service.getFactory(TorrentSourcingFactory.class).queue(optionalUserSource.get());
            return DiscordResponse.success("La vérification va être effectuée sous peu.");
        }

        if (optionalDefaultSource.isPresent()) {
            this.service.getFactory(TorrentSourcingFactory.class).queue(optionalDefaultSource.get());
            return DiscordResponse.success("La vérification va être effectuée sous peu.");
        }

        return DiscordResponse.error("Aucune source disponible pour le téléchargement automatique.");
    }
    // </editor-fold>

    // <editor-fold desc="@ task/reset-list ─ Create the watchlists within the configured channel.">
    @Interact(
            name = "task/reset-list",
            description = "\uD83D\uDD12 — Créé les watchlist dans le salon configuré."
    )
    public SlashResponse resetLists(UserEntity user) {

        this.service.getFactory(WatchlistCreateFactory.class).queue(Task.PRIORITY_MANUAL_HIGH);
        return DiscordResponse.success(
                "Les listes ont été réinitialisée. Il vous faudra supprimer les messages des anciennes listes.");
    }
    // </editor-fold>

    // <editor-fold desc="@ task/refresh-lists ─ Refresh all watchlists.">
    @Interact(
            name = "task/refresh-lists",
            description = "\uD83D\uDD12 — Force l'actualisation des listes."
    )
    public SlashResponse refreshLists(UserEntity user) {

        requireAdministrator(user);

        WatchlistUpdateFactory factory = this.service.getFactory(WatchlistUpdateFactory.class);

        for (AnimeList list : AnimeList.collect(AnimeList.Property.SHOW)) {
            factory.queue(list, Task.PRIORITY_MANUAL_HIGH);
        }

        return DiscordResponse.success("Les listes vont être actualisée.");
    }
    // </editor-fold>

    // <editor-fold desc="@ task/purge-torrents ─ Delete the torrent files following the retention setting.">
    @Interact(
            name = "task/purge-torrents",
            description = "\uD83D\uDD12 — Supprime les fichiers de torrents selon l'option de rétention."
    )
    public SlashResponse purgeTorrents(UserEntity user) {

        requireAdministrator(user);
        this.service.getFactory(TorrentRetentionControlFactory.class).queue();
        return DiscordResponse.success("Les torrents vont être nettoyés.");
    }
    // </editor-fold>


    // <editor-fold desc="@ task/import-file ─ Import a media file as an episode for an anime">
    @Interact(
            name = "task/import-file",
            description = "\uD83D\uDD12 — Importe un fichier media en tant qu'épisode d'un anime.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel le fichier sera importé",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "file",
                            autoCompleteName = "importable:files",
                            description = "Fichier à importer",
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "episode",
                            description = "Numéro de l'épisode correspondant au fichier",
                            type = OptionType.INTEGER,
                            required = true
                    )
            }
    )
    public SlashResponse importMediaFile(UserEntity user, @Param("anime") long animeId, @Param("file") String file, @Param("episode") long episodeNumber) {

        requireAdministrator(user);
        Anime anime  = this.animeService.fetch(animeId);
        Path  source = this.library.getResolver(Library.IMPORTS).file(file);

        if (!Files.isRegularFile(source)) {
            return DiscordResponse.error("Le fichier choisi n'est pas valide.");
        }

        long max = Math.abs(anime.getTotal());

        if (episodeNumber > max) {
            return DiscordResponse.warn("Le nombre d'épisode maximum pour cet anime est de **%s**".formatted(max));
        }

        Episode episode = anime.getEpisodes()
                               .stream()
                               .filter(item -> item.getNumber() == episodeNumber)
                               .findFirst()
                               .orElseGet(() -> this.episodeService.create(anime, (int) episodeNumber));

        Task task = this.service.getFactory(MediaImportFactory.class).queue(source, episode);

        return DiscordResponse.info(String.format(
                "L'épisode **%s** de l'anime **%s** va être importé.\nTâche: `%s`",
                episode.getNumber(),
                DiscordUtils.link(anime),
                task.getName()
        ));
    }
    // </editor-fold>

}
