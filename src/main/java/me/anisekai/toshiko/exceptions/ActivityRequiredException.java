package me.anisekai.toshiko.exceptions;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ActivityRequiredException extends RuntimeException implements DiscordEmbeddable {

    public ActivityRequiredException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("Désolé, mais tu ne peux effectuer cette action.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }
}
