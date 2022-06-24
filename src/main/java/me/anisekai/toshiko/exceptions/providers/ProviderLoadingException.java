package me.anisekai.toshiko.exceptions.providers;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ProviderLoadingException extends RuntimeException implements DiscordEmbeddable {

    public ProviderLoadingException(Throwable cause) {

        super(cause);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Une erreur est survenue lors de la récupération des informations de l'anime.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
