package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.components.RankingHandler;
import me.anisekai.modules.shizue.utils.LeaderboardUtils;
import me.anisekai.modules.toshiko.Texts;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@InteractionBean
public class LeaderboardInteractions {

    private final RankingHandler ranking;

    public LeaderboardInteractions(RankingHandler ranking) {

        this.ranking = ranking;
    }

    // <editor-fold desc="@ top/anime [order: string, limit: ?integer]">
    @Interact(
            name = "top/anime",
            description = Texts.TOP_ANIME__DESCRIPTION,
            options = {
                    @Option(
                            name = "order",
                            description = Texts.TOP_ANIME__OPTION_ORDER,
                            type = OptionType.STRING,
                            required = true,
                            choices = {
                                    @Choice(
                                            id = "ASC",
                                            display = Texts.TOP_ANIME__OPTION_ORDER__CHOICE_ASC
                                    ),
                                    @Choice(
                                            id = "DESC",
                                            display = Texts.TOP_ANIME__OPTION_ORDER__CHOICE_DESC
                                    )
                            }
                    ),
                    @Option(
                            name = "limit",
                            description = Texts.TOP_ANIME__OPTION_LIMIT,
                            type = OptionType.INTEGER
                    )
            }
    )
    public SlashResponse topAnime(@Param("order") String order, @Param("limit") Long limit) {

        Map<Anime, Double> animeScore = this.ranking.getAnimeScore();
        long               count      = Optional.ofNullable(limit).orElse(5L);

        EmbedBuilder builder = new EmbedBuilder();

        String download;
        if (order.equalsIgnoreCase("DESC")) {
            download = LeaderboardUtils.getTopDescFormatted(animeScore, AnimeStatus.DOWNLOADED, count);
        } else {
            download = LeaderboardUtils.getTopAscFormatted(animeScore, AnimeStatus.DOWNLOADED, count);
        }

        builder.setDescription(download);
        return new SimpleResponse(builder, false, false);
    }
    // </editor-fold>

    // <editor-fold desc="@ top/user">
    @Interact(
            name = "top/user",
            description = Texts.TOP_USER__DESCRIPTION
    )
    public SlashResponse topUser() {

        EmbedBuilder  builder     = new EmbedBuilder();
        StringBuilder description = new StringBuilder();
        AtomicInteger counter     = new AtomicInteger(1);

        this.ranking.getUserPowerMap().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> description.append(
                            String.format(
                                    "**%s.** %s  ─  %.2f  ─ %s\n\n",
                                    counter.getAndIncrement(), // Position
                                    entry.getKey().getEmote(), // Icon
                                    entry.getValue() * 100, // Vote power
                                    UserSnowflake.fromId(entry.getKey().getId()).getAsMention() // @user
                            )
                    ));

        builder.setDescription(description);
        return new SimpleResponse(builder, false, false);
    }
    // </editor-fold>

}
