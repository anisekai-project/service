package me.anisekai.toshiko.messages.responses;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class SimpleResponse implements SlashResponse, ButtonResponse {

    private final Collection<EmbedBuilder> builders;
    private final boolean                  edit;
    private final boolean                  ephemeral;

    public SimpleResponse(String message, boolean edit, boolean ephemeral) {

        this(new EmbedBuilder().setDescription(message), edit, ephemeral);
    }

    public SimpleResponse(EmbedBuilder builder, boolean edit, boolean ephemeral) {

        this.builders  = Collections.singletonList(builder);
        this.edit      = edit;
        this.ephemeral = ephemeral;
    }

    public SimpleResponse(Collection<EmbedBuilder> builders, boolean edit, boolean ephemeral) {

        this.builders  = builders;
        this.edit      = edit;
        this.ephemeral = ephemeral;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return this.edit;
    }

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (amb) -> amb.setEmbeds(this.builders.stream().map(EmbedBuilder::build).toList());
    }

    @Override
    public boolean isEphemeral() {

        return this.ephemeral;
    }

}
