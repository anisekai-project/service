package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.services.UserService;
import me.anisekai.toshiko.services.responses.SimpleResponse;
import me.anisekai.toshiko.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@InteractionBean
public class LeaderboardInteractions {

    private final AnimeService animeService;
    private final UserService  userService;

    public LeaderboardInteractions(AnimeService animeService, UserService userService) {

        this.animeService = animeService;
        this.userService  = userService;
    }

    // <editor-fold desc="@ top/anime">
    @Interact(
            name = "top/anime",
            description = "Affiche un classement (top 5) sur les votes d'animes",
            options = {
                    @Option(
                            name = "order",
                            description = "Ordre de tri",
                            type = OptionType.STRING,
                            required = true,
                            choices = {
                                    @Choice(
                                            id = "ASC",
                                            display = "Top des moins votés"
                                    ),
                                    @Choice(
                                            id = "DESC",
                                            display = "Top des mieux votés"
                                    )
                            }
                    )
            }
    )
    public SlashResponse topAnime(@Param("order") String order) {

        Map<Anime, Double> animeVotes = this.animeService.getAnimeVotes();

        EmbedBuilder builder = new EmbedBuilder();
        String       simulcast;
        String       download;
        if (order.equalsIgnoreCase("DESC")) {
            simulcast = DiscordUtils.getTopDescFormatted(this.animeService, animeVotes, AnimeStatus.SIMULCAST_AVAILABLE, 5);
            download  = DiscordUtils.getTopDescFormatted(this.animeService, animeVotes, AnimeStatus.DOWNLOADED, 5);
        } else {
            simulcast = DiscordUtils.getTopAscFormatted(this.animeService, animeVotes, AnimeStatus.SIMULCAST_AVAILABLE, 5);
            download  = DiscordUtils.getTopAscFormatted(this.animeService, animeVotes, AnimeStatus.DOWNLOADED, 5);
        }

        builder.addField(AnimeStatus.SIMULCAST_AVAILABLE.getDisplay(), simulcast, false);
        builder.addBlankField(false);
        builder.addField(AnimeStatus.DOWNLOADED.getDisplay(), download, false);

        return new SimpleResponse(builder, false, false);
    }
    // </editor-fold>


    // <editor-fold desc="@ top/user">
    @Interact(
            name = "top/user",
            description = "Affiche un classement de puissance de vote"
    )
    public SlashResponse topUser() {

        EmbedBuilder  builder     = new EmbedBuilder();
        StringBuilder description = new StringBuilder();
        AtomicInteger counter     = new AtomicInteger(1);

        this.userService.getVotePercentage().entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> {
                            description
                                    .append("**")
                                    .append(counter.getAndIncrement())
                                    .append(".** ")
                                    .append(entry.getKey().getEmote())
                                    .append(" ─ ")
                                    .append(String.format("%.2f", entry.getValue() * 100))
                                    .append(" ─ ")
                                    .append(UserSnowflake.fromId(entry.getKey().getId()).getAsMention())
                                    .append("\n\n");
                        });

        builder.setDescription(description);
        return new SimpleResponse(builder, false, false);
    }
    // </editor-fold>


}
