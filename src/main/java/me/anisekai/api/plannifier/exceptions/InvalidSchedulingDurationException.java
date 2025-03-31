package me.anisekai.api.plannifier.exceptions;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class InvalidSchedulingDurationException extends RuntimeException implements DiscordEmbeddable {

    public InvalidSchedulingDurationException() {

        super("Unable to schedule an event with an invalid duration.");
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Impossible de plannifier un évènement avec une durée invalide.");
        embed.setColor(Color.RED);

        return embed;
    }

    /**
     * In case this {@link DiscordEmbeddable} is an {@link Exception}, check if the message should be displayed to
     * everyone. If {@code false}, the message will be ephemeral.
     *
     * @return True if public, false otherwise.
     */
    @Override
    public boolean showToEveryone() {

        return true;
    }

}
