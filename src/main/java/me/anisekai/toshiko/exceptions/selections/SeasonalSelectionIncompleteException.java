package me.anisekai.toshiko.exceptions.selections;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.SeasonalSelection;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalSelectionIncompleteException extends RuntimeException implements DiscordEmbeddable {

    private final SeasonalSelection seasonalSelection;

    public SeasonalSelectionIncompleteException(SeasonalSelection seasonalSelection) {

        this.seasonalSelection = seasonalSelection;
    }

    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
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
