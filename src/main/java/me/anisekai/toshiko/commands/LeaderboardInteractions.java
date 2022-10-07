package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
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

    private final ToshikoService toshikoService;

    public LeaderboardInteractions(ToshikoService toshikoService) {

        this.toshikoService = toshikoService;
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

        Map<Anime, Double> animeVotes = this.toshikoService.getAnimeVotes();

        EmbedBuilder builder = new EmbedBuilder();
        String       simulcast;
        String       download;
        if (order.equalsIgnoreCase("DESC")) {
            simulcast = DiscordUtils.getTopDescFormatted(animeVotes, AnimeStatus.SIMULCAST_AVAILABLE, 5);
            download  = DiscordUtils.getTopDescFormatted(animeVotes, AnimeStatus.DOWNLOADED, 5);
        } else {
            simulcast = DiscordUtils.getTopAscFormatted(animeVotes, AnimeStatus.SIMULCAST_AVAILABLE, 5);
            download  = DiscordUtils.getTopAscFormatted(animeVotes, AnimeStatus.DOWNLOADED, 5);
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

        this.toshikoService.getInterestPower().getUserInterestPower().entrySet().stream()
                           .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                           .forEach(entry -> {
                               description.append(
                                       String.format(
                                               "**%s.** %s  ─  %.2f  ─ %s\n\n",
                                               counter.getAndIncrement(), // Position
                                               entry.getKey().getEmote(), // Icon
                                               entry.getValue() * 100, // Vote power
                                               UserSnowflake.fromId(entry.getKey().getId()).getAsMention() // @user
                                       )
                               );
                           });

        builder.setDescription(description);
        return new SimpleResponse(builder, false, false);
    }
    // </editor-fold>

}
