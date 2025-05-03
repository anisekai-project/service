package me.anisekai.discord.responses.embeds;

import fr.anisekai.wireless.remote.interfaces.SelectionEntity;
import me.anisekai.Texts;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Voter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectionEmbed extends EmbedBuilder {


    public void asClosed(SelectionEntity<Anime> selection, Iterable<Voter> voters) {

        this.setTitle(Texts.formatted(selection.getSeason(), selection.getYear()), "https://anichart.net/");
        this.setDescription("Les votes pour cette saison sont terminés !");

        for (Voter voter : voters) {
            DiscordUser user = voter.getUser();

            String username = String.format("%s %s", user.getEmote(), user.getUsername());
            String votes = voter.getVotes()
                                .stream()
                                .map(anime -> String.format("- [%s](%s)", anime.getTitle(), anime.getUrl()))
                                .collect(Collectors.joining("\n"));

            this.addField(username, votes, true);
        }
    }

    public void asOpened(SelectionEntity<Anime> selection, Collection<Voter> voters) {

        this.setTitle(Texts.formatted(selection.getSeason(), selection.getYear()), "https://anichart.net/");
        this.setDescription("Les votes pour cette saison sont ouverts !");

        Map<Anime, DiscordUser> animeVotingMap = new HashMap<>();


        String participants = voters.stream()
                                    .peek(voter -> voter.getVotes()
                                                        .forEach(anime -> animeVotingMap.put(anime, voter.getUser())))
                                    .map(voter -> String.format(
                                            "-【 %s 】— %s vote(s) — <@%s>",
                                            voter.getUser().getEmote(),
                                            voter.getAmount(),
                                            voter.getUser().getId()
                                    ))
                                    .collect(Collectors.joining("\n"));

        String animes = selection.getAnimes().stream()
                                 .map(anime -> {
                                     if (animeVotingMap.containsKey(anime)) {
                                         DiscordUser voter = animeVotingMap.get(anime);
                                         return String.format(
                                                 "- `%s` — %s [%s](%s)",
                                                 voter.getEmote(),
                                                 anime.getId(),
                                                 anime.getTitle(),
                                                 anime.getUrl()
                                         );
                                     } else {
                                         return String.format(
                                                 "- **`%s` — [%s](%s)**",
                                                 anime.getId(),
                                                 anime.getTitle(),
                                                 anime.getUrl()
                                         );
                                     }
                                 })
                                 .collect(Collectors.joining("\n"));

        this.addField("Votants", participants, false);
        this.addField("Animes", animes, false);
    }

}
