package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.io.DiskService;
import me.anisekai.toshiko.io.ToshikoFileSystem;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;

@Component
@InteractionBean
public class MiscellaneousInteractions {

    private final ToshikoService    toshikoService;
    private final DiskService       diskService;
    private final ToshikoFileSystem fileSystem;

    public MiscellaneousInteractions(ToshikoService toshikoService, DiskService diskService, ToshikoFileSystem fileSystem) {

        this.toshikoService = toshikoService;
        this.diskService    = diskService;
        this.fileSystem     = fileSystem;
    }

    // <editor-fold desc="@ refresh">
    @Interact(
            name = "refresh",
            description = Texts.REFRESH__DESCRIPTION,
            options = {
                    @Option(
                            name = "target",
                            description = Texts.REFRESH__OPTION_TARGET,
                            type = OptionType.STRING,
                            required = true,
                            choices = {
                                    @Choice(
                                            id = "watchlist",
                                            display = Texts.REFRESH__OPTION_TARGET__CHOICE_WATCHLIST
                                    ),
                                    @Choice(
                                            id = "announce",
                                            display = Texts.REFRESH__OPTION_TARGET__CHOICE_ANNOUNCE
                                    ),
                                    @Choice(
                                            id = "schedule",
                                            display = Texts.REFRESH__OPTION_TARGET__CHOICE_SCHEDULE
                                    )
                            }
                    ),
                    @Option(
                            name = "force",
                            description = Texts.REFRESH__OPTION_FORCE,
                            type = OptionType.BOOLEAN
                    )
            }
    )
    public SlashResponse refreshWatchlist(DiscordUser user, @Param("target") String target, @Param("force") Boolean force) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Désolé, mais tu ne peux pas faire ça.", false, false);
        }

        switch (target) {
            case "watchlist" -> {
                this.toshikoService.queueUpdateAll(force);
                return new SimpleResponse("Les listes seront actualisées sous peu.", false, false);
            }
            case "announce" -> {
                this.toshikoService.getAnimeRepository().findAll().stream()
                                   .filter(anime -> anime.getAnnounceMessage() != null)
                                   .forEach(this.toshikoService::refreshAnimeAnnounce);
                return new SimpleResponse("Les annonces seront actualisées sous peu.", false, false);
            }
            case "schedule" -> {
                this.toshikoService.refreshSchedule();
                return new SimpleResponse("Les évènements seront actualisés sous peu.", false, false);
            }
            default -> {
                return new SimpleResponse("Hmmm, quelque chose s'est mal passé.", false, false);
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ disk/cache ─ Rebuild the anime cache">
    @Interact(
            name = "disk/cache",
            description = "Rebuild the anime cache",
            defer = true
    )
    public SlashResponse rebuildCache(DiscordUser user) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Seul un administrateur peut faire ça.", false, false);
        }

        ZonedDateTime start = ZonedDateTime.now();
        this.diskService.cache();
        ZonedDateTime end = ZonedDateTime.now();

        long duration = Duration.between(start, end).getSeconds();
        long amount   = this.diskService.getDatabase().size();

        return new SimpleResponse(String.format("Il y a `%s` anime sur le disque (détecté en `%s`s).", amount, duration), false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ disk/import ─ Import anime from the 'automation' directory.">
    @Interact(
            name = "disk/import",
            description = "Import anime from the 'automation' directory.",
            defer = true
    )
    public SlashResponse runImport(DiscordUser user) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Seul un administrateur peut faire ça.", false, false);
        }

        int amount = this.fileSystem.checkForAutomation();
        int queued = this.fileSystem.getAmountInQueue();

        return new SimpleResponse(String.format(
                "Compris ! Voici un résumé de ce qu'il vient de se passer:\n`--` Nouveau(x) fichier(s) à importer: %s\n`--` Fichier(s) en cours d'import: %s",
                amount,
                queued
        ), false, false);
    }
    // </editor-fold>
}
