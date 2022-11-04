package me.anisekai.toshiko.exceptions.nights;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.Anime;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class OverlappingScheduleException extends RuntimeException implements DiscordEmbeddable {

    private static final String ERR = "Impossible de programmer la scéance pour l'anime **%s**: L'heure choisie ferait que cette scénace entrerait en conflit avec une autre.";

    private final Anime anime;

    public OverlappingScheduleException(Anime anime) {

        this.anime = anime;
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(String.format(ERR, this.anime.getName()));
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
