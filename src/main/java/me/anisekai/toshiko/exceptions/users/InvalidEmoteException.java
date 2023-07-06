package me.anisekai.toshiko.exceptions.users;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class InvalidEmoteException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Ce n'est pas un emoji valide")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
