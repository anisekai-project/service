package me.anisekai.toshiko.events.misc;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.Torrent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.awt.*;
import java.time.OffsetDateTime;

public class TorrentFinishedEvent extends ApplicationEvent implements DiscordEmbeddable {

    private final Torrent torrent;
    private final boolean otherDownloading;

    public TorrentFinishedEvent(Object source, Torrent torrent, boolean otherDownloading) {

        super(source);
        this.torrent = torrent;
        this.otherDownloading = otherDownloading;
    }

    public Torrent getTorrent() {

        return this.torrent;
    }

    public boolean isOtherDownloading() {

        return this.otherDownloading;
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        return new EmbedBuilder()
                .setTitle("Un torrent a été téléchargé")
                .setDescription(String.format(
                        "Le torrent `%s` a été téléchargé.\n[Voir sur Nyaa](%s)",
                        this.torrent.getName(),
                        this.torrent.getLink()
                ))
                .setFooter(this.getSource().getClass().getName())
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.GREEN);
    }

    /**
     * In case this {@link DiscordEmbeddable} is an {@link Exception}, check if the message should be displayed to
     * everyone. If {@code false}, the message will be ephemeral.
     *
     * @return True if public, false otherwise.
     */
    @Override
    public boolean showToEveryone() {

        return true;
    }

}
