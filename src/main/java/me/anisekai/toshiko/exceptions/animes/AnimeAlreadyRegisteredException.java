package me.anisekai.toshiko.exceptions.animes;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AnimeAlreadyRegisteredException extends RuntimeException implements DiscordEmbeddable {

    private final String name;

    public AnimeAlreadyRegisteredException(String name) {

        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("L'anime ")
                .appendDescription(this.getName())
                .appendDescription(" a déjà été ajouté.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }
}
