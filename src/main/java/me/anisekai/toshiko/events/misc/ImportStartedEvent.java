package me.anisekai.toshiko.events.misc;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.awt.*;
import java.time.OffsetDateTime;

public class ImportStartedEvent extends ApplicationEvent implements DiscordEmbeddable {

    private final int amount;

    public ImportStartedEvent(Object source, int amount) {

        super(source);
        this.amount = amount;
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setTitle("Un import a démarré")
                .setDescription(String.format(
                        "%s fichier%s %s être importé%s.",
                        this.amount,
                        this.amount == 1 ? "" : "s",
                        this.amount == 1 ? "va" : "vont",
                        this.amount == 1 ? "" : "s"
                ))
                .setFooter(this.getSource().getClass().getName())
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.MAGENTA);
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
