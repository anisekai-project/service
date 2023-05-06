package me.anisekai.toshiko.exceptions.selections;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.SeasonalSelection;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalSelectionClosedException extends RuntimeException implements DiscordEmbeddable {

    private final SeasonalSelection seasonalSelection;

    public SeasonalSelectionClosedException(SeasonalSelection seasonalSelection) {

        this.seasonalSelection = seasonalSelection;
    }

    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
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
