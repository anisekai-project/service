package me.anisekai.toshiko.modules.discord.messages.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.entities.SeasonalVoter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SeasonalSelectionEmbed implements SlashResponse, ButtonResponse {

    private final SeasonalSelection  seasonalSelection;
    private final Set<SeasonalVote>  votes;
    private final Set<SeasonalVoter> voters;

    public SeasonalSelectionEmbed(SeasonalSelection seasonalSelection) {

        this.seasonalSelection = seasonalSelection;
        this.votes             = seasonalSelection.getVotes()
                                                  .stream()
                                                  .sorted(Comparator.comparing(vote -> vote.getAnime().getId()))
                                                  .collect(Collectors.toCollection(LinkedHashSet::new));

        this.voters = seasonalSelection.getVoters()
                                       .stream()
                                       .sorted(
                                               Comparator.comparing(SeasonalVoter::getAmount)
                                                         .reversed()
                                                         .thenComparing(voter -> voter.getUser().getId())
                                       )
                                       .collect(Collectors.toCollection(LinkedHashSet::new));


    }

    public EmbedBuilder getClosed() {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(String.format("# Saison: %s\n", this.seasonalSelection.getName()));
        embedBuilder.appendDescription("Les membres ont fait leur choix!\n");

        for (SeasonalVoter voter : this.voters) {

            List<SeasonalVote> votes = this.votes.stream()
                                                 .filter(vote -> vote.getUser().equals(voter.getUser()))
                                                 .toList();

            embedBuilder.appendDescription(String.format(
                    "- %s (<@%s>)\n",
                    voter.getUser().getEmote(),
                    voter.getUser().getId()
            ));

            for (SeasonalVote vote : votes) {
                embedBuilder.appendDescription(String.format(
                        "  - [%s](%s)\n",
                        vote.getAnime().getName(),
                        vote.getAnime().getLink()
                ));
            }

            embedBuilder.appendDescription("\n");
        }

        return embedBuilder;
    }

    public EmbedBuilder getIntroduction() {
        // Build data for user visibility
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setDescription(String.format("# Saison: %s\n", this.seasonalSelection.getName()));


        this.voters.stream()
                   .sorted(
                           Comparator.comparingInt(SeasonalVoter::getAmount)
                                     .reversed()
                                     .thenComparingLong(voter -> voter.getUser().getId())
                   ).map(voter -> String.format(
                    "- %s - %s choix - (<@%s>)\n",
                    voter.getUser().getEmote(),
                    voter.getAmount(),
                    voter.getUser().getId()
            )).forEach(embedBuilder::appendDescription);

        return embedBuilder;
    }

    public EmbedBuilder getAnimeList() {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("# Les Simulcasts:\n");

        this.seasonalSelection.getAnimes().stream()
                              .sorted(Comparator.comparingLong(Anime::getId))
                              .forEach(anime -> {
                                  String line = this.getVoteFor(anime).map(vote -> String.format(
                                          "- ~~`[ %s ]` %s [%s](%s)~~",
                                          anime.getId(),
                                          vote.getUser().getEmote(),
                                          anime.getName(),
                                          anime.getLink()
                                  )).orElseGet(() -> String.format(
                                          "- **`[ %s ]` [%s](%s)**",
                                          anime.getId(),
                                          anime.getName(),
                                          anime.getLink()
                                  ));

                                  embedBuilder.appendDescription(line).appendDescription("\n");
                              });

        return embedBuilder;
    }

    private Optional<SeasonalVote> getVoteFor(Anime anime) {

        return this.votes
                .stream()
                .filter(vote -> vote.getAnime().equals(anime))
                .findFirst();
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (mr) -> {

            if (this.seasonalSelection.isClosed()) {
                mr.setEmbeds(this.getClosed().build());
                mr.setComponents(Collections.emptyList());
                return;
            }


            mr.setEmbeds(this.getIntroduction().build(), this.getAnimeList().build());

            // Buttons time ! yay !
            List<Button> buttons = this.seasonalSelection.getAnimes().stream().map(anime ->
                                                                                           this.getVoteFor(anime)
                                                                                               .map(vote -> Button.of(
                                                                                                       ButtonStyle.SECONDARY,
                                                                                                       String.format(
                                                                                                               "button://season/cast?seasonal=%s&anime=%s",
                                                                                                               this.seasonalSelection.getId(),
                                                                                                               anime.getId()
                                                                                                       ),
                                                                                                       String.format(
                                                                                                               "Anime %s",
                                                                                                               anime.getId()
                                                                                                       ),
                                                                                                       Emoji.fromUnicode(
                                                                                                               Objects.requireNonNull(
                                                                                                                       vote.getUser()
                                                                                                                           .getEmote()))
                                                                                               ))
                                                                                               .orElseGet(() -> Button.of(
                                                                                                       ButtonStyle.PRIMARY,
                                                                                                       String.format(
                                                                                                               "button://season/cast?seasonal=%s&anime=%s",
                                                                                                               this.seasonalSelection.getId(),
                                                                                                               anime.getId()
                                                                                                       ),
                                                                                                       String.format(
                                                                                                               "Anime %s",
                                                                                                               anime.getId()
                                                                                                       )
                                                                                               ))).toList();

            Collection<ActionRow> rows = new ArrayList<>(ActionRow.partitionOf(buttons));
            rows.add(ActionRow.of(Button.danger(String.format(
                    "button://season/close?seasonal=%s",
                    this.seasonalSelection.getId()
            ), "Clôturer")));

            mr.setComponents(rows);
        };
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }

}
