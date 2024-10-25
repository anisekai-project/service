package me.anisekai.modules.toshiko.messages.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.shizue.entities.Interest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.function.Consumer;

public class InterestResponse implements ButtonResponse, SlashResponse {

    private final Interest interest;
    private final boolean  editOriginal;

    public InterestResponse(Interest interest, boolean editOriginal) {

        this.interest     = interest;
        this.editOriginal = editOriginal;
    }

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (mr) -> {

            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("Ton niveau d'intérêt pour cet anime a bien été mis à jour.");

            if (!this.interest.getUser().isActive()) {
                builder.appendDescription("\n");
                builder.appendDescription(
                        "Cependant, comme tu n'es pas considéré(e) comme une personne active, ton vote n'aura aucune influence sur le classement.");
            }

            builder.addField("Anime", this.interest.getAnime().getName(), false);
            builder.addField("Niveau d'intérêt", this.interest.getLevel().getDisplayText(), false);


            mr.setEmbeds(builder.build());
        };
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return this.editOriginal;
    }

    @Override
    public boolean isEphemeral() {

        return !this.editOriginal;
    }

}
