package me.anisekai.discord.interactions.administation;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import me.anisekai.discord.annotations.InteractionBean;
import me.anisekai.discord.exceptions.RequireAdministratorException;
import me.anisekai.discord.responses.DiscordResponse;
import me.anisekai.discord.tasks.watchlist.create.WatchlistCreateFactory;
import me.anisekai.discord.tasks.watchlist.update.WatchlistUpdateFactory;
import me.anisekai.library.tasks.factories.TorrentSourcingFactory;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.SettingService;
import me.anisekai.server.services.TaskService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@InteractionBean
public class TaskInteractions {

    private final TaskService    service;
    private final SettingService settingService;

    public TaskInteractions(TaskService service, SettingService settingService) {

        this.service        = service;
        this.settingService = settingService;
    }

    private static void requireAdministrator(UserEntity user) {

        if (!user.isAdministrator()) {
            throw new RequireAdministratorException();
        }
    }

    // <editor-fold desc="@ task/check-automation ─ Import media files within the automation folder.">
    @Interact(
            name = "task/check-automation",
            description = "\uD83D\uDD12 — Importe les fichier média présents dans le dossier d'automatisation.",
            defer = true
    )
    public SlashResponse checkAutomation(UserEntity user) {

        //TODO
        return new DiscordResponse("TODO: Do something and write a response");
    }
    // </editor-fold>

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

}
