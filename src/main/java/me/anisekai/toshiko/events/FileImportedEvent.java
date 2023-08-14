package me.anisekai.toshiko.events;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.awt.*;
import java.time.OffsetDateTime;

public class FileImportedEvent extends ApplicationEvent implements DiscordEmbeddable {

    private final String path;

    public FileImportedEvent(Object source, String path) {

        super(source);
        this.path = path;
    }


    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setTitle("Un fichier a été importé")
                .setDescription(String.format("Le fichier `%s` a été importé.", this.path))
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
