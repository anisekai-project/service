package me.anisekai.toshiko.exceptions;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class JdaUnavailableException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder().setDescription("*Une erreur s'est produite lors de la connexion à Discord.*").setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
