package me.anisekai.toshiko.exceptions.providers;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class StupidUserException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Bon, il va falloir te soigner. Il faut un lien d'une fiche **d'anime**, *pas de manga*, connard")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }
}
