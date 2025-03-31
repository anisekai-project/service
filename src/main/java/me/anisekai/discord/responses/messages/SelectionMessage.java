package me.anisekai.discord.responses.messages;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.discord.responses.embeds.SelectionEmbed;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.entities.Voter;
import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.interfaces.IDiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.*;
import java.util.function.Consumer;

public class SelectionMessage implements SlashResponse, ButtonResponse {

    private final Selection         selection;
    private final Collection<Voter> voters;

    public SelectionMessage(Selection selection, Collection<Voter> voters) {

        this.selection = selection;
        this.voters    = voters;
    }

    /**
     * Check if instead of sending a new message, the original {@link Message} on which the {@link Button} has been
     * clicked should be edited.
     *
     * @return True if the original {@link Message} should be edited, false otherwise.
     */
    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }

    /**
     * Retrieve the {@link MessageRequest} {@link Consumer} that should set the response content.
     *
     * @return A {@link MessageRequest} {@link Consumer}
     */
    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return mr -> {
            SelectionEmbed selectionEmbed = new SelectionEmbed();

            if (this.selection.getStatus().isClosed()) {
                selectionEmbed.asClosed(this.selection, this.voters);
            } else {
                selectionEmbed.asOpened(this.selection, this.voters);
                Map<Anime, DiscordUser> votes = new HashMap<>();

                for (Voter voter : this.voters) {
                    voter.getVotes().forEach(anime -> votes.put(anime, voter.getUser()));
                }

                List<Button> buttons = this.selection.getAnimes()
                                                     .stream()
                                                     .sorted(Comparator.comparingLong(IAnime::getId))
                                                     .map(anime -> votes.containsKey(anime) ? this.asButton(
                                                             anime,
                                                             votes.get(anime)
                                                     ) : this.asButton(anime))
                                                     .toList();

                Collection<Button> allButtons = new ArrayList<>(buttons);

                allButtons.add(Button.of(
                        ButtonStyle.DANGER,
                        String.format("button://selection/close?selection=%s", this.selection.getId()),
                        "Clôturer"
                ));

                mr.setComponents(ActionRow.partitionOf(allButtons));
            }
        };
    }

    /**
     * Check if this {@link SlashResponse} is ephemeral (ie: Only shown to the user who interacted).
     *
     * @return True if ephemeral, false otherwise.
     */
    @Override
    public boolean isEphemeral() {

        return false;
    }

    private Button asButton(IAnime<?> anime) {

        return Button.primary(
                String.format("button://vote?selection=%s&anime=%s", this.selection.getId(), anime.getId()),
                String.valueOf(anime.getId())
        );
    }

    private Button asButton(IAnime<?> anime, IDiscordUser votedBy) {

        return Button.of(
                ButtonStyle.SECONDARY,
                String.format("button://vote?selection=%s&anime=%s", this.selection.getId(), anime.getId()),
                String.valueOf(anime.getId()),
                Emoji.fromUnicode(Objects.requireNonNull(votedBy.getEmote()))
        );

    }

}
