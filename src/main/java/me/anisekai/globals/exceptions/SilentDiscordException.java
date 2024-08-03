package me.anisekai.globals.exceptions;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.AnisekaiApplication;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.ZonedDateTime;

public abstract class SilentDiscordException extends RuntimeException implements DiscordEmbeddable {

    public SilentDiscordException(String message, Throwable cause) {

        super(message, cause);
    }

    public SilentDiscordException(String message) {

        super(message);
    }

    @NotNull
    public abstract String getFriendlyMessage();

    private String getDiscordMessage() {

        if (AnisekaiApplication.enableDetailedOutput) {
            return "%s\n\n> %s".formatted(this.getFriendlyMessage(), this.getMessage());
        }
        return this.getFriendlyMessage();
    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setAuthor("Anisekai", null, "https://anisekai.fr/favicon.png")
                .setDescription(this.getDiscordMessage())
                .setColor(Color.RED)
                .setTimestamp(ZonedDateTime.now());
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
