package me.anisekai.toshiko.exceptions.seasonalvoter;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalVoterNotFoundException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setDescription("L'utilisateur de la selection demandé n'a pas été trouvé.");

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
