package me.anisekai.toshiko.exceptions.broadcast;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class BroadcastNotFoundException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setDescription("La séance demandée n'a pas été trouvée.");

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
