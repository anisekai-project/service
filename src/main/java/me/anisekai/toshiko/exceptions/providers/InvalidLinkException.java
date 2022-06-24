package me.anisekai.toshiko.exceptions.providers;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class InvalidLinkException extends RuntimeException implements DiscordEmbeddable {

    private final String link;

    public InvalidLinkException(String link) {

        this.link = link;
    }

    public String getLink() {

        return this.link;
    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Impossible de traiter le lien fourni.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
