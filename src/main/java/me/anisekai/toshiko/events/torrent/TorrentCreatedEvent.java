package me.anisekai.toshiko.events.torrent;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.events.EntityCreatedEvent;
import me.anisekai.toshiko.interfaces.LoggableEvent;
import me.anisekai.toshiko.utils.Embedding;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TorrentCreatedEvent extends EntityCreatedEvent<Torrent> implements LoggableEvent {

    public TorrentCreatedEvent(Object source, Torrent entity) {

        super(source, entity);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return Embedding.event(this)
                .setTitle("Un torrent a été ajouté")
                .setDescription(String.format(
                        "Le torrent %s a été ajouté et sera importé dès la fin de son téléchargement.",
                        Embedding.link(this.getEntity().getName(), this.getEntity().getLink())
                ))
                .setColor(Color.ORANGE);
    }

}
