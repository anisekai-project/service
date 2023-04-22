package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
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
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@InteractionBean
public class AnimeInteractions {

    private static final Logger         LOGGER = LoggerFactory.getLogger(AnimeInteractions.class);
    private final        ToshikoService toshikoService;

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
                            autoComplete = true
                    )
            }
    )
    public SlashResponse sendAnimeNotification(DiscordUser discordUser, @Param("anime") Long animeId) {

        if (!discordUser.isAdmin()) {
            return new SimpleResponse("Tu n'as pas le droit de faire ça.", false, false);
        }

        if (animeId != null) {
            Anime anime = this.toshikoService.findAnime(animeId);
            if (anime.getAnnounceMessage() == null) {
                this.toshikoService.createAnimeAnnounce(anime);
            } else {
                this.toshikoService.refreshAnimeAnnounce(anime);
            }
            return new SimpleResponse("La notification sera envoyée sous peu.", false, false);
        } else {
            List<Anime> all = this.toshikoService.getAnimeRepository().findAllByStatusIn(AnimeStatus.getDisplayable());
            all.forEach(anime -> {
                if (anime.getAnnounceMessage() == null) {
                    this.toshikoService.createAnimeAnnounce(anime);
                } else {
                    this.toshikoService.refreshAnimeAnnounce(anime);
                }
            });
            return new SimpleResponse(String.format("%s annonces vont être envoyées.", all.size()), false, false);
        }
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

        Anime              anime      = this.toshikoService.findAnime(animeId);
        Map<Anime, Double> animeVotes = this.toshikoService.getAnimeVotes();

        AnimeEmbed sheetMessage = new AnimeEmbed(anime, animeVotes.getOrDefault(anime, 0.0));
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
            return new SimpleResponse("Avant de pouvoir vote pour un anime, tu dois définir ton icône de vote. (`/profile`)", false, true);
        }

        InterestLevel level    = InterestLevel.from(interestName);
        Interest      interest = this.toshikoService.setInterestLevel(animeId, user, level);


        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Ton niveau d'interêt pour cet anime a bien été mis à jour.");

        if (!discordUser.isActive()) {
            builder.appendDescription("\n");
            builder.appendDescription("Cependant comme tu n'es pas considéré(e) comme une personne active, ton vote n'aura aucune influence sur le classement.");
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

        JSONObject   json       = new JSONObject(rawJson);
        JSONArray    genreArray = json.getJSONArray("genres");
        JSONArray    themeArray = json.getJSONArray("themes");
        String       rawStatus  = json.getString("status");
        List<String> genres     = new ArrayList<>();
        List<String> themes     = new ArrayList<>();

        genreArray.forEach(obj -> genres.add(obj.toString()));
        themeArray.forEach(obj -> themes.add(obj.toString()));

        AnimeStatus status = AnimeStatus.from(rawStatus);
        long        total  = Long.parseLong(json.getString("episode"));
        long        time   = Long.parseLong(json.getString("time"));

        Anime loaded = new Anime();
        loaded.setName(json.getString("title"));
        loaded.setSynopsis(json.getString("synopsis"));
        loaded.setGenres(String.join(", ", genres));
        loaded.setThemes(String.join(", ", themes));
        loaded.setStatus(status);
        loaded.setLink(json.getString("link"));
        loaded.setImage(json.getString("image"));
        loaded.setWatched(0);
        loaded.setTotal(total);
        loaded.setEpisodeDuration(time == 0 ? 24 : time);
        loaded.setAddedBy(user);
        loaded.setAddedAt(ZonedDateTime.now().withNano(0));

        Optional<Anime> byName = this.toshikoService.getAnimeRepository().findByName(loaded.getName());

        if (byName.isPresent()) {
            Anime anime = byName.get();
            anime.patch(loaded);
            this.toshikoService.getAnimeRepository().save(anime);
            this.toshikoService.refreshAnimeAnnounce(anime);
            return new SimpleResponse("L'anime a été mis à jour avec succès.", false, false);
        } else {
            Anime saved = this.toshikoService.getAnimeRepository().save(loaded);
            this.toshikoService.getInterestRepository().save(new Interest(saved, user, InterestLevel.INTERESTED));
            this.toshikoService.createAnimeAnnounce(saved);
            return new SimpleResponse("L'anime a été importé avec succès.", false, false);
        }
    }
    // </editor-fold>
}
