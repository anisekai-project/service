package me.anisekai.toshiko.services.responses;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

public class SimpleResponse implements SlashResponse, ButtonResponse {

    private final Message message;
    private final boolean edit;
    private final boolean ephemeral;

    public SimpleResponse(String message, boolean edit, boolean ephemeral) {

        this(new EmbedBuilder().setDescription(message), edit, ephemeral);
    }

    public SimpleResponse(EmbedBuilder builder, boolean edit, boolean ephemeral) {

        this(new MessageBuilder().setEmbeds(builder.build()).build(), edit, ephemeral);
    }

    public SimpleResponse(Message message, boolean edit, boolean ephemeral) {

        this.message   = message;
        this.edit      = edit;
        this.ephemeral = ephemeral;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return this.edit;
    }

    @Override
    public Message getMessage() {

        return this.message;
    }

    @Override
    public boolean isEphemeral() {

        return this.ephemeral;
    }

}
