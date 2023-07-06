package me.anisekai.toshiko.exceptions.selections;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalSelectionNotFoundException extends RuntimeException implements DiscordEmbeddable {

    public SeasonalSelectionNotFoundException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("Impossible de retrouver la selection saisonnière associée.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }

}
