package me.anisekai.toshiko.exceptions.torrent;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TorrentNotFoundException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setDescription("Le torrent demandé n'a pas été trouvé.");

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }
}
