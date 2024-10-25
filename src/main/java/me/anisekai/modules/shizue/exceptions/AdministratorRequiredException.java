package me.anisekai.modules.shizue.exceptions;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AdministratorRequiredException extends RuntimeException implements DiscordEmbeddable {

    public AdministratorRequiredException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("Seul un administrateur peut effectuer cette action.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }

}
