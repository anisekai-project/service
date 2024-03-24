package me.anisekai.modules.linn.exceptions.anime;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class InvalidAnimeProgressException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setDescription("La progression entrée est invalide.");

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
