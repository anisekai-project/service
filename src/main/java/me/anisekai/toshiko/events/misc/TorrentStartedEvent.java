package me.anisekai.toshiko.events.misc;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.Torrent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;

public class TorrentStartedEvent extends ApplicationEvent implements DiscordEmbeddable {

    private final List<Torrent> torrents;

    public TorrentStartedEvent(Object source, List<Torrent> torrents) {

        super(source);
        this.torrents = torrents;
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();

        if (this.torrents.size() == 1) {
            builder.setTitle("Un torrent a été ajouté.");
        } else {
            builder.setTitle(String.format("%s torrents ont été ajouté.", this.torrents.size()));
        }

        for (Torrent torrent : this.torrents) {
            builder.appendDescription(String.format("- [%s](%s)\n", torrent.getName(), torrent.getLink()));
        }

        builder.setColor(Color.CYAN);
        builder.setFooter(this.getSource().getClass().getName());
        builder.setTimestamp(OffsetDateTime.now());

        return builder;
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
