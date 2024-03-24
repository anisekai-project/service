package me.anisekai.modules.toshiko.messages.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.shizue.components.RankingHandler;
import me.anisekai.modules.shizue.entities.Watchlist;
import me.anisekai.modules.toshiko.messages.embeds.watchlist.WatchlistAnimePart;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WatchlistEmbed implements SlashResponse, ButtonResponse {

    private final Watchlist      watchlist;
    private final RankingHandler ranking;

    public WatchlistEmbed(Watchlist watchlist, RankingHandler ranking) {

        this.watchlist = watchlist;
        this.ranking   = ranking;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (amb) -> {
            EmbedBuilder builder = new EmbedBuilder();
            List<WatchlistAnimePart> animes = this.watchlist.getAnimes().stream()
                                                            .sorted()
                                                            .map(anime -> new WatchlistAnimePart(
                                                                    anime,
                                                                    this.ranking.getAnimeScore(
                                                                            anime)
                                                            ))
                                                            .toList();

            builder.setAuthor(String.format("%s (%s)", this.watchlist.getId().getDisplay(), animes.size()));

            if (animes.size() > 75) {
                builder.setDescription("*Il faudrait ptet arrêter d'ajouter des animes à ce niveau là hein...*");
            } else if (animes.size() > 25) {
                builder.setDescription(animes.stream()
                                             .map(WatchlistAnimePart::getMacroFormat)
                                             .collect(Collectors.joining("\n")));
            } else if (animes.isEmpty()) {
                builder.setDescription("*Aucun anime présent dans cette liste...*");
            } else {
                String full = animes.stream().map(WatchlistAnimePart::getFullFormat).collect(Collectors.joining("\n"));
                String small = animes.stream()
                                     .map(WatchlistAnimePart::getSmallFormat)
                                     .collect(Collectors.joining("\n"));
                String macro = animes.stream()
                                     .map(WatchlistAnimePart::getMacroFormat)
                                     .collect(Collectors.joining("\n"));

                String output = Stream.of(full, small, macro)
                                      .filter(opt -> opt.length() < MessageEmbed.DESCRIPTION_MAX_LENGTH)
                                      .findFirst()
                                      .orElse("*Il faudrait ptet arrêter d'ajouter des animes à ce niveau là hein...*");

                builder.setDescription(output);
            }

            builder.setTimestamp(ZonedDateTime.now());
            amb.setEmbeds(builder.build());
        };
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }

}
