package me.anisekai.toshiko.exceptions.seasonalvote;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class UserVotingForbiddenException extends RuntimeException implements DiscordEmbeddable {

    public UserVotingForbiddenException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("Tu n'es pas autorisé à choisir un simulcast pour cette saison.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
