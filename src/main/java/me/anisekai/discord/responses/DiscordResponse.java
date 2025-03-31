package me.anisekai.discord.responses;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class DiscordResponse implements SlashResponse, ButtonResponse {

    private final Collection<EmbedBuilder> builders;
    private final boolean                  edit;
    private final boolean                  ephemeral;

    public DiscordResponse(CharSequence message) {

        this(message, false, false);
    }

    public DiscordResponse(CharSequence message, boolean edit, boolean ephemeral) {

        this(new EmbedBuilder().setDescription(message), edit, ephemeral);
    }

    public DiscordResponse(EmbedBuilder builder, boolean edit, boolean ephemeral) {

        this.builders  = Collections.singletonList(builder);
        this.edit      = edit;
        this.ephemeral = ephemeral;
    }

    public DiscordResponse(Collection<EmbedBuilder> builders, boolean edit, boolean ephemeral) {

        this.builders  = builders;
        this.edit      = edit;
        this.ephemeral = ephemeral;
    }

    public static DiscordResponse of(CharSequence message, Color color) {

        return new DiscordResponse(new EmbedBuilder().setDescription(message).setColor(color), false, false);
    }

    public static DiscordResponse error(String message, Object... args) {

        return of(String.format(message, args), Color.RED);
    }

    public static DiscordResponse warn(String message, Object... args) {

        return of(String.format(message, args), Color.ORANGE);
    }

    public static DiscordResponse info(String message, Object... args) {

        return of(String.format(message, args), Color.CYAN);
    }

    public static DiscordResponse success(String message, Object... args) {

        return of(String.format(message, args), Color.GREEN);
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
