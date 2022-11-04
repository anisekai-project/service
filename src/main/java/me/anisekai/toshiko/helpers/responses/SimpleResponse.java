package me.anisekai.toshiko.helpers.responses;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.function.Consumer;

public class SimpleResponse implements SlashResponse, ButtonResponse {

    private final EmbedBuilder builder;
    private final boolean      edit;
    private final boolean      ephemeral;

    public SimpleResponse(String message, boolean edit, boolean ephemeral) {

        this(new EmbedBuilder().setDescription(message), edit, ephemeral);
    }

    public SimpleResponse(EmbedBuilder builder, boolean edit, boolean ephemeral) {

        this.builder   = builder;
        this.edit      = edit;
        this.ephemeral = ephemeral;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return this.edit;
    }

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return (amb) -> amb.setEmbeds(this.builder.build());
    }

    @Override
    public boolean isEphemeral() {

        return this.ephemeral;
    }

}
