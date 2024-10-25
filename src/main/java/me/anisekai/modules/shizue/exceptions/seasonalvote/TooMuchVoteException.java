package me.anisekai.modules.shizue.exceptions.seasonalvote;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TooMuchVoteException extends RuntimeException implements DiscordEmbeddable {

    public TooMuchVoteException() {

    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .appendDescription("Tu as atteint ton nombre de vote max.")
                .setColor(Color.RED);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
