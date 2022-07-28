package me.anisekai.toshiko.exceptions.interests;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class InterestLevelUnchangedException extends RuntimeException implements DiscordEmbeddable {

    private final Interest interest;

    public InterestLevelUnchangedException(Interest interest) {

        this.interest = interest;
    }

    public Anime getAnime() {

        return this.interest.getAnime();
    }

    public DiscordUser getUser() {

        return this.interest.getUser();
    }

    public Interest getInterest() {

        return this.interest;
    }

    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setDescription("Ton niveau d'interêt reste inchangé.")
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
