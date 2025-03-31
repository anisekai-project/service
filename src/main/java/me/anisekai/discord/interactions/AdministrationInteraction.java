package me.anisekai.discord.interactions;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.api.plannifier.data.CalibrationResult;
import me.anisekai.discord.annotations.InteractAt;
import me.anisekai.discord.annotations.InteractionBean;
import me.anisekai.discord.exceptions.RequireAdministratorException;
import me.anisekai.discord.responses.DiscordResponse;
import me.anisekai.discord.responses.messages.SelectionMessage;
import me.anisekai.discord.tasks.anime.announcement.AnnouncementFactory;
import me.anisekai.discord.utils.InteractionType;
import me.anisekai.server.entities.*;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.enums.BroadcastFrequency;
import me.anisekai.server.enums.SelectionStatus;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.services.*;
import me.anisekai.utils.DateTimeUtils;
import me.anisekai.utils.StringUtils;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@InteractionBean
public class AdministrationInteraction {

    private final AnimeService     animeService;
    private final BroadcastService broadcastService;
    private final SelectionService selectionService;
    private final SettingService   settingService;
    private final TaskService      taskService;
    private final VoterService     voterService;

    public AdministrationInteraction(TaskService taskService, AnimeService animeService, BroadcastService broadcastService, SelectionService selectionService, SettingService settingService, VoterService voterService) {

        this.taskService      = taskService;
        this.animeService     = animeService;
        this.broadcastService = broadcastService;
        this.selectionService = selectionService;
        this.settingService   = settingService;
        this.voterService     = voterService;
    }

    private static void requireAdministrator(IDiscordUser user) {

        if (!user.isAdministrator()) {
            throw new RequireAdministratorException();
        }
    }

    // <editor-fold desc="@ anime/title-match ─ Allow to change the title regex of an anime to match titles on the nyaa.si website. [anime: integer, regex: string]">
    @Interact(
            name = "anime/title-match",
            description = "\uD83D\uDD12 — Défini la regex permettant de trouver la source de téléchargement sur nyaa.si.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel la regex sera définie.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "regex",
                            description = "Regex permettant de match le titre de l'anime.",
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse setAnimeTitleMatch(IDiscordUser user, @Param("anime") long animeId, @Param("regex") String regex) {

        requireAdministrator(user);
        Anime anime = this.animeService.mod(animeId, entity -> entity.setTitleRegex(regex));
        return DiscordResponse.info("L'anime **%s** a été mis à jour.\nRegex: `%s`", anime.getTitle(), regex);
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/announce ─ Send an announcement message for the provided anime. [anime: integer]">
    @Interact(
            name = "anime/announce",
            description = "\uD83D\uDD12 — Envoi un message d'annonce pour l'anime spécifié",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel l'annonce sera envoyée",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse announceAnime(IDiscordUser user, @Param("anime") long animeId) {

        requireAdministrator(user);
        Anime anime = this.animeService.fetch(animeId);
        this.taskService.getFactory(AnnouncementFactory.class).queue(anime, Task.PRIORITY_MANUAL_LOW);
        if (anime.getAnnouncementId() == null) {
            return DiscordResponse.info("L'annonce pour l'anime **%s** sera envoyée d'ici peu.", anime.getTitle());
        } else {
            return DiscordResponse.info("L'annonce pour l'anime **%s** sera mise à jour d'ici peu.", anime.getTitle());
        }
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/status ─ Change to which watchlist an anime belongs. [anime: integer, watchlist: string]">
    @Interact(
            name = "anime/status",
            description = "\uD83D\uDD12 — Change à quelle watchlist appartient un anime.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel la watchlist sera changée.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "watchlist",
                            description = "Nouvelle watchlist pour l'anime",
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse animeStatusUpdate(IDiscordUser user, @Param("anime") long animeId, @Param("watchlist") String status) {

        requireAdministrator(user);
        Anime anime = this.animeService.mod(animeId, entity -> entity.setWatchlist(AnimeStatus.from(status)));
        return DiscordResponse.info(
                "La watchlist de l'anime **%s** a bien été changée.\n%s",
                anime.getTitle(),
                anime.getWatchlist().getDisplay()
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/progress ─ Change the watch progress of an anime. [anime: integer, progress: integer]">
    @Interact(
            name = "anime/status",
            description = "\uD83D\uDD12 — Change à quelle watchlist appartient un anime.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel la watchlist sera changée.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "progress",
                            description = "Nouvelle progression de visionnage pour l'anime",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
            }
    )
    public SlashResponse animeProgress(IDiscordUser user, @Param("anime") long animeId, @Param("progress") long progress) {

        requireAdministrator(user);
        Anime anime = this.animeService.mod(animeId, entity -> entity.setWatched(progress));
        return DiscordResponse.info(
                "La progression de l'anime **%s** a bien été mis à jour.\n%s épisode(s) regardé(s)",
                anime.getTitle(),
                progress
        );
    }
    // </editor-fold>

    // --

    // <editor-fold desc="@ broadcast/schedule ─ Schedule one or more watch party. [anime: integer, time: string, amount: string, frequency: ?string, starting: ?string]">
    @Interact(
            name = "broadcast/schedule",
            description = "\uD83D\uDD12 — Planifie une séance ou plusieurs séances de visionnage.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel la ou les séances seront planifiées.",
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "time",
                            description = "Heure à laquelle sera planifié la séance. (Format: HH:MM)",
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "amount",
                            description = "Nombre d'épisode à planifier pour chaque séance.",
                            type = OptionType.INTEGER,
                            required = true
                    ),
                    @Option(
                            name = "frequency",
                            description = "Fréquence à laquelle sera planifié chaque séance.",
                            type = OptionType.STRING,
                            autoComplete = true
                    ),
                    @Option(
                            name = "starting",
                            description = "Date à partir de laquelle les séances seront planifiées. (Format: JJ/MM/AAAA)",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse broadcastSchedule(IDiscordUser user, @Param("anime") long animeId, @Param("frequency") String frequencyName, @Param("time") String timeParam, @Param("amount") long amount, @Param("starting") String startingParam) {

        requireAdministrator(user);
        Anime anime = this.animeService.fetch(animeId);

        BroadcastFrequency frequency = BroadcastFrequency.from(frequencyName);
        ZonedDateTime      starting  = DateTimeUtils.of(timeParam, startingParam);

        if (DateTimeUtils.isBeforeOrEquals(starting, DateTimeUtils.now())) {
            return DiscordResponse.error("Impossible de plannifier des séances dans le passé.");
        }

        List<Broadcast> scheduled = this.broadcastService.schedule(anime, starting, frequency, amount);

        return DiscordResponse.info(StringUtils.count(
                scheduled.size(),
                "Aucune séance n'a été plannifiée.",
                "Une séance a été plannifiée.",
                "%s ont été plannifiées."
        ));
    }
    // </editor-fold>

    // <editor-fold desc="@ broadcast/calibrate ─ Run a manual calibration of all watch party.">
    @Interact(
            name = "broadcast/calibrate",
            description = "\uD83D\uDD12 — Permet de lancer une calibration manuelle des séances.",
            defer = true
    )
    public SlashResponse broadcastCalibration(IDiscordUser user) {

        //TODO
        return new DiscordResponse("TODO: Do something and write a response");
    }
    // </editor-fold>

    // <editor-fold desc="@ broadcast/delay ─ Delay one or multiple scheduled watch party. [delay: string, range: ?string, starting: ?string">
    @Interact(
            name = "broadcast/delay",
            description = "\uD83D\uDD12 — Permet de reporter une ou plusieurs séances planifiées.",
            defer = true,
            options = {
                    @Option(
                            name = "delay",
                            description = "La durée de report des séances. (Format: 0j00h00m)",
                            type = OptionType.STRING,
                            required = true
                    ),
                    @Option(
                            name = "range",
                            description = "Durée de l'interval d'action du report de séances. (Par défaut: 6h) (Format: 0j00h00m)",
                            type = OptionType.STRING
                    ),
                    @Option(
                            name = "starting",
                            description = "Heure à partir de laquelle l'interval commencera. (Par Défaut: Heure actuelle) (Format: HH:MM)",
                            type = OptionType.STRING
                    )
            }
    )
    public SlashResponse broadcastDelay(IDiscordUser user, @Param("delay") String delayParam, @Param("range") String rangeParam, @Param("starting") String startingParam) {

        CalibrationResult calibrationResult = this.broadcastService.createScheduler().calibrate();
        return DiscordResponse.info(
                "Calibration réussi: %s évènement(s) mis à jour, %s évènement(s) supprimé(s).",
                calibrationResult.getUpdateCount(),
                calibrationResult.getDeleteCount()
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ broadcast/cancel ─ Cancel a watch party that has already started.">
    @Interact(
            name = "broadcast/cancel",
            description = "\uD83D\uDD12 — Annule une séance de visionnage déjà en cours."
    )
    public SlashResponse broadcastCancel() {

        int amount = this.broadcastService.cancel();

        return DiscordResponse.info(StringUtils.count(
                amount,
                "Aucun évènement annulé.",
                "Un évènement a été annulé.",
                "%s évènements ont été annulés"
        ));
    }
    // </editor-fold>

    // <editor-fold desc="@ broadcast/refresh ─ Refresh all scheduled event with the database status.">
    @Interact(
            name = "broadcast/refresh",
            description = "\uD83D\uDD12 — Actualise les évènements plannifiés du serveur."
    )
    public SlashResponse broadcastRefresh(IDiscordUser user) {

        int amount = this.broadcastService.refresh();

        return DiscordResponse.info(StringUtils.count(
                amount,
                "Aucun évènement actualisé.",
                "Un évènement a été actualisé.",
                "%s évènements ont été actualisés"
        ));
    }
    // </editor-fold>

    // --

    // <editor-fold desc="@ selection/create ─ Démarre une séléction d'anime pour la saison en cours.">
    @Interact(
            name = "selection/create",
            description = "\uD83D\uDD12 — Démarre une séléction d'anime pour la saison en cours.",
            options = {
                    @Option(
                            name = "votes",
                            description = "Nombre de vote au total pour la selection. (Par défaut: 8)",
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse createSelection(IDiscordUser user, @Param("votes") Long votesParam) {

        requireAdministrator(user);

        long        votes     = Optional.ofNullable(votesParam).orElse(8L);
        Selection   selection = this.selectionService.createSelection();
        List<Voter> voters    = this.voterService.createVoters(selection, votes);

        return new SelectionMessage(selection, voters);
    }
    // </editor-fold>

    // <editor-fold desc="█ selection/close ─ Ferme les votes pour la selection en cours. [selection: integer]">
    @Interact(
            name = "selection/close",
            description = "\uD83D\uDD12 — Ferme les votes pour la selection en cours.",
            options = {
                    @Option(
                            name = "selection",
                            description = "",
                            type = OptionType.INTEGER,
                            required = true
                    )
            }
    )
    @InteractAt(InteractionType.BUTTON)
    public ButtonResponse closeSelection(IDiscordUser user, @Param("selection") long selectionId) {

        requireAdministrator(user);

        Selection selection = this.selectionService.mod(
                selectionId,
                entity -> entity.setStatus(SelectionStatus.CLOSED)
        );

        List<Voter> voters = this.voterService.getVoters(selection);

        return new SelectionMessage(selection, voters);
    }
    // </editor-fold>

    // --

    // <editor-fold desc="@ settings/watchlist-channel ─ Défini le salon qui sera utilisé pour les watchlists. [channel: Channel]">
    @Interact(
            name = "settings/watchlist-channel",
            description = "\uD83D\uDD12 — Défini le salon qui sera utilisé pour les watchlists.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "channel",
                            description = "Le salon a définir pour cette option",
                            type = OptionType.CHANNEL,
                            required = true
                    )
            }
    )
    public SlashResponse settingWatchlistChannel(IDiscordUser user, @Param("channel") Channel channel) {

        return new DiscordResponse("TODO: Do something and write a response");
    }
    // </editor-fold>

    // <editor-fold desc="@ settings/announcement-channel ─ Défini le salon qui sera utilisé pour les annonces. [channel: Channel]">
    @Interact(
            name = "settings/announcement-channel",
            description = "\uD83D\uDD12 — Défini le salon qui sera utilisé pour les annonces.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "channel",
                            description = "Le salon a définir pour cette option",
                            type = OptionType.CHANNEL,
                            required = true
                    )
            }
    )
    public SlashResponse settingAnnouncementChannel(IDiscordUser user, @Param("channel") Channel channel) {

        requireAdministrator(user);

        if (channel.getType() == ChannelType.TEXT) {
            this.settingService.setSetting(SettingService.ANNOUNCEMENT_CHANNEL, channel.getId());
            return DiscordResponse.info("Les annonces d'anime seront envoyées dans %s.", channel.getAsMention());
        }

        return DiscordResponse.error("Merci de choisir un channel textuel pour envoyer les annonces.");
    }
    // </editor-fold>

    // <editor-fold desc="@ settings/audit-channel ─ Défini le salon qui sera utilisé pour les messages d'administration. [channel: ?Channel]">
    @Interact(
            name = "settings/audit-channel",
            description = "\uD83D\uDD12 — Défini le salon qui sera utilisé pour les messages d'administration.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "channel",
                            description = "Le salon a définir pour cette option",
                            type = OptionType.CHANNEL,
                            required = true
                    )
            }
    )
    public SlashResponse settingAuditChannel(IDiscordUser user, @Param("channel") Channel channel) {

        requireAdministrator(user);

        if (channel.getType() == ChannelType.TEXT) {
            this.settingService.setSetting(SettingService.AUDIT_CHANNEL, channel.getId());
            return DiscordResponse.info(
                    "Les messages d'administration seront envoyés dans %s.",
                    channel.getAsMention()
            );
        }

        return DiscordResponse.error("Merci de choisir un channel textuel pour envoyer les messages d'administration.");
    }
    // </editor-fold>

    // <editor-fold desc="@ settings/enable-auto-download ─ Active ou désactive le téléchargement automatique des épisodes. [value: boolean]">
    @Interact(
            name = "settings/enable-auto-download",
            description = "\uD83D\uDD12 — Active ou désactive le téléchargement automatique des épisodes.",
            target = SlashTarget.ALL,
            options = {
                    @Option(
                            name = "value",
                            description = "Valeur de l'option",
                            type = OptionType.BOOLEAN,
                            required = true
                    )
            }
    )
    public SlashResponse settingAutoDownload(IDiscordUser user, @Param("value") boolean value) {

        requireAdministrator(user);

        this.settingService.setSetting(SettingService.DOWNLOAD_ENABLED, Boolean.toString(value));
        return DiscordResponse.info("Les téléchargements automatiques ont été %s.", value ? "activés" : "désactivés");
    }
    // </editor-fold>

    // --

    // <editor-fold desc="@ tasks/check-automation ─ Importe les épisodes présents dans le dossier d'automatisation.">
    @Interact(
            name = "tasks/check-automation",
            description = "\uD83D\uDD12 — Importe les épisodes présents dans le dossier d'automatisation.",
            defer = true
    )
    public SlashResponse checkAutomation(IDiscordUser user) {

        //TODO
        return new DiscordResponse("TODO: Do something and write a response");
    }
    // </editor-fold>

    // <editor-fold desc="@ tasks/check-downloads ─ Vérifie si des épisodes peuvent être téléchargés">
    @Interact(
            name = "tasks/check-downloads",
            description = "\uD83D\uDD12 — Vérifie si des épisodes peuvent être téléchargés.",
            defer = true
    )
    public SlashResponse checkDownloads(IDiscordUser user) {

        //TODO
        return new DiscordResponse("TODO: Do something and write a response");
    }
    // </editor-fold>

}
