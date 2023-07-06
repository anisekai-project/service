package me.anisekai.toshiko.exceptions.selections;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AnimeAlreadyVotedException extends RuntimeException implements DiscordEmbeddable {

    public AnimeAlreadyVotedException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("Cet anime est déjà choisi par quelqu'un d'autre.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
