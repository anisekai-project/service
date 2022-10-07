package me.anisekai.toshiko.helpers.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.helpers.containers.VariablePair;
import me.anisekai.toshiko.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class WatchlistEmbed implements SlashResponse, ButtonResponse {

    private final Watchlist          watchlist;
    private final Map<Anime, Double> animeVotes;

    public WatchlistEmbed(Watchlist watchlist, Map<Anime, Double> animeVotes) {

        this.watchlist  = watchlist;
        this.animeVotes = animeVotes;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }

    @Override
    public Consumer<AbstractMessageBuilder<?, ?>> getHandler() {

        return (amb) -> {
            EmbedBuilder builder = new EmbedBuilder();
            Set<Anime>   animes  = this.watchlist.getAnimes();

            builder.setAuthor(String.format("%s (%s)", this.watchlist.getStatus().getDisplay(), animes.size()));

            StringBuilder withLinks    = new StringBuilder();
            StringBuilder withoutLinks = new StringBuilder();

            for (Anime anime : animes) {

                VariablePair<String, String> result = DiscordUtils.buildAnimeList(anime);

                String entryWithLink    = result.getFirst();
                String entryWithoutLink = result.getSecond();

                withLinks.append(entryWithLink).append("\n\n");
                withoutLinks.append(entryWithoutLink).append("\n\n");
            }

            if (withLinks.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
                builder.setDescription(withoutLinks);
            } else {
                builder.setDescription(withLinks);
            }

            builder.setFooter("Dernière actualisation le");
            builder.setTimestamp(LocalDateTime.now());

            amb.setEmbeds(builder.build());
        };
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }
}
