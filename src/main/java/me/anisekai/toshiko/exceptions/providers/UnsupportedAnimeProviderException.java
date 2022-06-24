package me.anisekai.toshiko.exceptions.providers;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.net.URI;

public class UnsupportedAnimeProviderException extends RuntimeException implements DiscordEmbeddable {

    private final URI uri;

    public UnsupportedAnimeProviderException(URI uri) {

        this.uri = uri;
    }

    public URI getUri() {

        return this.uri;
    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Désolé, mais le lien fourni n'est pas compatible.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }
}
