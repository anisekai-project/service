package me.anisekai.toshiko.exceptions.users;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class EmojiAlreadyUsedException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Cet emoji est déjà utilisé.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
