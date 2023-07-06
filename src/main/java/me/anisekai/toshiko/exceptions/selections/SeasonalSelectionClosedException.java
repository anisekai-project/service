package me.anisekai.toshiko.exceptions.selections;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalSelectionClosedException extends RuntimeException implements DiscordEmbeddable {

    public SeasonalSelectionClosedException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setTitle("Impossible d'effectuer cette action")
                .setDescription("La sélection est terminée et ne peut plus être modifiée.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }

}
