package me.anisekai.toshiko.messages.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.entities.SeasonalVoter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class SeasonalSelectionEmbed implements SlashResponse, ButtonResponse {

    private final SeasonalSelection seasonalSelection;

    public SeasonalSelectionEmbed(SeasonalSelection seasonalSelection) {

        this.seasonalSelection = seasonalSelection;
    }

    public EmbedBuilder getIntroduction() {
        // Build data for user visibility
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Une saison démarre ! " + this.seasonalSelection.getName());
        embedBuilder.setDescription("""
                                    Les votes sont ouvert ! Choisissez le ou les anime(s) que vous souhaitez visionner.
                                                                   
                                    Ci-dessous, le nombre de votes par personne:
                                    """);

        for (SeasonalVoter voter : this.seasonalSelection.getVoters()) {
            embedBuilder.addField(String.format("%s (%s)",
                    voter.getUser().getUsername(),
                    voter.getUser().getEmote()
            ), String.format("%s choix possible(s)", voter.getAmount()), false);
        }

        return embedBuilder;
    }

    public EmbedBuilder getAnimeList() {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Voici les animes qui sont disponible pour cette saison:");

        for (Anime anime : this.seasonalSelection.getAnimes()) {
            embedBuilder.addField(this.asField(anime));
        }

        return embedBuilder;
    }

    private MessageEmbed.Field asField(Anime anime) {

        return this.getVoteFor(anime).map(vote -> new MessageEmbed.Field(
                String.format("%s • ID: %s", vote.getUser().getEmote(), anime.getId()),
                String.format("[%s](%s)", anime.getName(), anime.getLink()),
                false
        )).orElseGet(() -> new MessageEmbed.Field(
                String.format("ID: %s", anime.getId()),
                String.format("[%s](%s)", anime.getName(), anime.getLink()),
                false
        ));
    }

    private Optional<SeasonalVote> getVoteFor(Anime anime) {

        return this.seasonalSelection.getVotes()
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
            mr.setEmbeds(this.getIntroduction().build(), this.getAnimeList().build());

            // Buttons time ! yay !
            List<Button> buttons = this.seasonalSelection.getAnimes().stream().map(anime ->
                    this.getVoteFor(anime)
                        .map(vote -> Button.of(ButtonStyle.DANGER,
                                String.format("button://season/cast?seasonal=%s&anime=%s", this.seasonalSelection.getId(), anime.getId()),
                                String.format("Anime %s", anime.getId()),
                                Emoji.fromUnicode(Objects.requireNonNull(vote.getUser().getEmote()))
                        ))
                        .orElseGet(() -> Button.of(ButtonStyle.SUCCESS,
                                String.format("button://season/cast?seasonal=%s&anime=%s", this.seasonalSelection.getId(), anime.getId()),
                                String.format("Anime %s", anime.getId())
                        ))).toList();

            mr.setComponents(ActionRow.partitionOf(buttons));
        };
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }
}
