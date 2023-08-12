package me.anisekai.toshiko.exceptions.interest;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class InterestNotFoundException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setDescription("Le vote n'a pas été trouvée.");

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}