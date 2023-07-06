package me.anisekai.toshiko.exceptions.selections;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalSelectionIncompleteException extends RuntimeException implements DiscordEmbeddable {

    public SeasonalSelectionIncompleteException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Tous les participants n'ont pas encore voté.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }

}
