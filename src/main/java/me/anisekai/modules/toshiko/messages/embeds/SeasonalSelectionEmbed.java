package me.anisekai.modules.toshiko.messages.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.interfaces.IAnime;
import me.anisekai.modules.shizue.entities.SeasonalVote;
import me.anisekai.modules.shizue.entities.SeasonalVoter;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalSelection;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVote;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVoter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SeasonalSelectionEmbed implements SlashResponse, ButtonResponse {

    private static final String BUTTON_ID              = "button://season/cast?seasonal=%s&anime=%s";
    private static final String BUTTON_TEXT            = "Anime %s";
    private static final String CLAIMED_ANIME_LINE     = "- ~~`[ %s ]` %s [%s](%s)~~";
    private static final String UNCLAIMED_ANIME_LINE   = "- **`[ %s ]` [%s](%s)**";
    private static final String OPENED_VOTER_LINE      = "- %s - %s choix - (<@%s>)";
    private static final String CLOSED_VOTER_LINE      = "- %s (<@%s>)";
    private static final String CLOSED_VOTER_VOTE_LINE = "  - [%s](%s)";
    private static final String AUTO_CLOSED_ANIME_LINE = "- [%s](%s)";

    private final ISeasonalSelection seasonalSelection;
    private final Set<SeasonalVote>  votes;
    private final Set<SeasonalVoter> voters;

    public SeasonalSelectionEmbed(ISeasonalSelection seasonalSelection) {

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

    private Button getAnimeButton(@NotNull IAnime anime) {

        Optional<SeasonalVote> optionalVote = this.votes
                .stream()
                .filter(vote -> vote.getAnime().equals(anime))
                .findFirst();

        String      id    = BUTTON_ID.formatted(this.seasonalSelection.getId(), anime.getId());
        String      text  = BUTTON_TEXT.formatted(anime.getName());
        ButtonStyle style = optionalVote.isEmpty() ? ButtonStyle.PRIMARY : ButtonStyle.SECONDARY;
        UnicodeEmoji emoji = optionalVote.map(ISeasonalVote::getUser)
                                         .map(DiscordUser::getEmote)
                                         .map(Emoji::fromUnicode)
                                         .orElse(null);

        return Button.of(style, id, text, emoji);
    }

    private String getAnimeLine(@NotNull IAnime anime) {

        return this.votes
                .stream()
                .filter(vote -> vote.getAnime().equals(anime))
                .findFirst()
                .map(vote -> CLAIMED_ANIME_LINE.formatted(
                        anime.getId(),
                        vote.getUser().getEmote(),
                        anime.getName(),
                        anime.getLink()
                ))
                .orElseGet(() -> UNCLAIMED_ANIME_LINE.formatted(
                        anime.getId(),
                        anime.getName(),
                        anime.getLink()
                ));
    }

    private String getOpenedVoterLine(@NotNull ISeasonalVoter voter) {

        return OPENED_VOTER_LINE.formatted(voter.getUser().getEmote(), voter.getAmount(), voter.getUser().getId());
    }

    private String getClosedVoterLine(@NotNull ISeasonalVoter voter) {

        List<String> lines = new ArrayList<>();
        lines.add(CLOSED_VOTER_LINE.formatted(voter.getUser().getEmote(), voter.getUser().getId()));

        this.votes.stream()
                  .filter(vote -> vote.getUser().equals(voter.getUser()))
                  .map(ISeasonalVote::getAnime)
                  .forEach(anime -> lines.add(CLOSED_VOTER_VOTE_LINE.formatted(
                          anime.getName(),
                          anime.getLink()
                  )));

        return String.join("\n", lines);
    }

    private MessageEmbed getOpenedVoterEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(String.format("# Saison: %s\n", this.seasonalSelection.getName()));

        builder.appendDescription(
                this.voters.stream()
                           .map(this::getOpenedVoterLine)
                           .collect(Collectors.joining("\n"))
        );

        return builder.build();
    }

    private MessageEmbed getOpenedAnimeEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("# Les Simulcasts:\n");

        builder.appendDescription(
                this.seasonalSelection.getAnimes().stream()
                                      .sorted(Comparator.comparing(Anime::getId))
                                      .map(this::getAnimeLine)
                                      .collect(Collectors.joining("\n"))
        );

        return builder.build();
    }

    private MessageEmbed getClosed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(String.format("# Saison: %s\n", this.seasonalSelection.getName()));
        builder.appendDescription("Les membres ont fait leur choix!\n");

        builder.appendDescription(
                this.voters.stream()
                           .map(this::getClosedVoterLine)
                           .collect(Collectors.joining("\n\n"))
        );

        return builder.build();
    }

    private MessageEmbed getAutoClosed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(String.format("# Saison: %s\n", this.seasonalSelection.getName()));
        builder.appendDescription("Les membres ont fait leur choix... Eh bah non !\n");

        builder.appendDescription(
                this.seasonalSelection.getAnimes()
                                      .stream()
                                      .map(anime -> AUTO_CLOSED_ANIME_LINE.formatted(anime.getName(), anime.getLink()))
                                      .collect(Collectors.joining("\n"))
        );

        return builder.build();
    }


    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }


    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (mr) -> {

            switch (this.seasonalSelection.getState()) {
                case OPENED -> {
                    mr.setEmbeds(this.getOpenedVoterEmbed(), this.getOpenedAnimeEmbed());

                    List<Button> buttons = this.seasonalSelection.getAnimes().stream()
                                                                 .sorted(Comparator.comparing(Anime::getId))
                                                                 .map(this::getAnimeButton)
                                                                 .toList();

                    Collection<ActionRow> rows = new ArrayList<>(ActionRow.partitionOf(buttons));
                    rows.add(ActionRow.of(Button.danger(String.format(
                            "button://season/close?seasonal=%s",
                            this.seasonalSelection.getId()
                    ), "Clôturer")));

                    mr.setComponents(rows);
                }
                case CLOSED -> {
                    mr.setEmbeds(this.getClosed());
                }
                case AUTO_CLOSED -> {
                    mr.setEmbeds(this.getAutoClosed());
                }
            }
        };
    }


    @Override
    public boolean isEphemeral() {

        return false;
    }

}
