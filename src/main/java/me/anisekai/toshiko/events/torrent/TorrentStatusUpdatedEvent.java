package me.anisekai.toshiko.events.torrent;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.enums.TorrentStatus;
import me.anisekai.toshiko.events.EntityUpdatedEvent;
import me.anisekai.toshiko.interfaces.LoggableEvent;
import me.anisekai.toshiko.utils.Embedding;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TorrentStatusUpdatedEvent extends EntityUpdatedEvent<Torrent, TorrentStatus> implements LoggableEvent {

    public TorrentStatusUpdatedEvent(Object source, Torrent entity, TorrentStatus previous, TorrentStatus current) {

        super(source, entity, previous, current);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return Embedding.event(this)
                .setDescription(String.format(
                        "Le torrent %s a été mis à jour",
                        Embedding.link(this.getEntity().getName(), this.getEntity().getLink())
                ))
                .addField(
                        "Changement de status",
                        String.format("**%s** ► **%s**", this.getPrevious().name(), this.getCurrent().name()),
                        false
                ).setColor(Color.WHITE);
    }

}
