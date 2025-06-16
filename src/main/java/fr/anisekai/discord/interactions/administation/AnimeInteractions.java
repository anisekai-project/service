package fr.anisekai.discord.interactions.administation;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import fr.anisekai.Texts;
import fr.anisekai.discord.annotations.InteractionBean;
import fr.anisekai.discord.exceptions.RequireAdministratorException;
import fr.anisekai.discord.responses.DiscordResponse;
import fr.anisekai.discord.tasks.anime.announcement.create.AnnouncementCreateFactory;
import fr.anisekai.library.LibraryService;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Task;
import fr.anisekai.server.services.AnimeService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@InteractionBean
public class AnimeInteractions {

    private final TaskService    taskService;
    private final LibraryService libraryService;
    private final AnimeService   service;

    public AnimeInteractions(TaskService taskService, LibraryService libraryService, AnimeService service) {

        this.taskService    = taskService;
        this.libraryService = libraryService;
        this.service        = service;
    }

    private static void requireAdministrator(UserEntity user) {

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
    public SlashResponse setAnimeTitleMatch(UserEntity user, @Param("anime") long animeId, @Param("regex") String regex) {

        requireAdministrator(user);

        if (!regex.contains("?<ep>")) {
            return DiscordResponse.error("La regex doit contenir le groupe de capture pour le numéro d'épisode.");
        }

        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        } catch (Exception e) {
            return DiscordResponse.error("La regex utilisée n'est pas formattée correctement.");
        }

        Anime anime = this.service.mod(animeId, entity -> entity.setTitleRegex(pattern));
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
    public SlashResponse announceAnime(UserEntity user, @Param("anime") long animeId) {

        requireAdministrator(user);
        Anime anime = this.service.fetch(animeId);
        this.taskService.getFactory(AnnouncementCreateFactory.class).queue(anime, Task.PRIORITY_MANUAL_LOW);
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
    public SlashResponse animeStatusUpdate(UserEntity user, @Param("anime") long animeId, @Param("watchlist") String status) {

        requireAdministrator(user);
        Anime anime = this.service.mod(animeId, entity -> entity.setList(AnimeList.from(status)));
        return DiscordResponse.info(
                "La watchlist de l'anime **%s** a bien été changée.\n%s",
                anime.getTitle(),
                Texts.formatted(anime.getList())
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/progress ─ Set the amount of watched episode for an anime. [anime: integer, progress: integer]">
    @Interact(
            name = "anime/progress",
            description = "\uD83D\uDD12 — Défini le nombre d'épisode regardé pour un anime.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel le nombre d'épisodes visionnés sera changé.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "progress",
                            description = "Nouvelle progression de visionnage pour l'anime",
                            type = OptionType.INTEGER,
                            required = true
                    ),
            }
    )
    public SlashResponse animeProgress(UserEntity user, @Param("anime") long animeId, @Param("progress") long progress) {

        requireAdministrator(user);
        Anime anime = this.service.mod(animeId, entity -> entity.setWatched(progress));
        return DiscordResponse.info(
                "La progression de l'anime **%s** a bien été mis à jour.\n%s épisode(s) regardé(s)",
                anime.getTitle(),
                progress
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/total ─ Set the amount of total episode for an anime. [anime: integer, total: integer]">
    @Interact(
            name = "anime/total",
            description = "\uD83D\uDD12 — Défini le nombre d'épisode total pour un anime.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel le nombre d'épisodes total sera changé.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "total",
                            description = "Nombre total d'épisode pour l'anime",
                            type = OptionType.INTEGER,
                            required = true
                    ),
            }
    )
    public SlashResponse animeTotal(UserEntity user, @Param("anime") long animeId, @Param("total") long total) {

        requireAdministrator(user);
        Anime anime = this.service.mod(animeId, entity -> entity.setTotal(total));
        return DiscordResponse.info(
                "Le nombre total d'épisode de l'anime **%s** a bien été mis à jour.\n%s épisode(s) au total %s",
                anime.getTitle(),
                total < 0 ? total * -1 : total,
                total < 0 ? "*(Estimation)*" : ""
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/duration ─ Set the amount of time an episode last. [anime: integer, duration: integer]">
    @Interact(
            name = "anime/duration",
            description = "\uD83D\uDD12 — Défini le temps de visionnage pour un épisode d'un anime.",
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel la watchlist sera changée.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "duration",
                            description = "Durée (en minute) pour un épisode",
                            type = OptionType.INTEGER,
                            required = true
                    ),
            }
    )
    public SlashResponse animeDuration(UserEntity user, @Param("anime") long animeId, @Param("duration") long duration) {

        requireAdministrator(user);
        Anime anime = this.service.mod(animeId, entity -> entity.setEpisodeDuration(duration));
        return DiscordResponse.info(
                "La durée d'un épisode pour l'anime **%s** a bien été mis à jour.\n%s minutes",
                anime.getTitle(),
                duration
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/bulk-status ─ Move all anime from one list to another. [from: string, to: string]">
    @Interact(
            name = "anime/bulk-status",
            description = "\uD83D\uDD12 — Déplace tous les animes d'une liste à une autre.",
            options = {
                    @Option(
                            name = "from",
                            autoCompleteName = "watchlist",
                            description = "Liste source",
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "to",
                            autoCompleteName = "watchlist",
                            description = "Liste de destination",
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse animeStatusBulkUpdate(UserEntity user, @Param("from") String from, @Param("to") String to) {

        requireAdministrator(user);

        AnimeList source      = AnimeList.from(from);
        AnimeList destination = AnimeList.from(to);

        List<Anime> animes = this.service.batch(
                repo -> repo.findAllByList(source),
                entity -> entity.setList(destination)
        );

        return DiscordResponse.info(
                "La watchlist de **%s** anime(s) a bien été changée.\n%s **->** %s",
                animes.size(),
                Texts.formatted(source),
                Texts.formatted(destination)
        );
    }
    // </editor-fold>

    // <editor-fold desc="@ anime/event-image ─ Change the event image for an anime. [anime: integer, duration: integer]">
    @Interact(
            name = "anime/event-image",
            description = "\uD83D\uDD12 — Change l'image d'évènement d'un anime.",
            defer = true,
            options = {
                    @Option(
                            name = "anime",
                            description = "Anime pour lequel l'image sera changée.",
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "image",
                            description = "Image d'évènement pour l'anime",
                            type = OptionType.ATTACHMENT,
                            required = true
                    ),
            }
    )
    public SlashResponse animeEventImage(UserEntity user, @Param("anime") long animeId, @Param("image") Message.Attachment attachment) throws Exception {

        requireAdministrator(user);

        if (!attachment.isImage() || !"png".equals(attachment.getFileExtension())) {
            return DiscordResponse.error("Merci de fournir une image.\n***800x320 (png)***");
        }

        if (attachment.getWidth() != 800 || attachment.getHeight() != 320) {
            return DiscordResponse.error(String.format(
                    "Les dimensions de l'image ne sont pas valide.\nReçu: %sx%s\nAttendu: 800x320",
                    attachment.getWidth(),
                    attachment.getHeight()
            ));
        }

        Anime anime = this.service.fetch(animeId);
        this.libraryService.storeAnimeEventImage(anime, attachment.getUrl());

        return DiscordResponse.info("L'image a bien été mise à jour.").setImage(attachment.getProxyUrl());
    }
    // </editor-fold>
}
