package me.anisekai.modules.freya.events.torrent;

import me.anisekai.globals.events.LoggableEvent;
import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.freya.entities.Torrent;
import me.anisekai.globals.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TorrentCreatedEvent extends EntityCreatedEvent<Torrent> implements LoggableEvent {

    public TorrentCreatedEvent(Object source, Torrent entity) {

        super(source, entity);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return DiscordUtils.event(this)
                           .setTitle("Un torrent a été ajouté")
                           .setDescription(String.format(
                                   "Le torrent %s a été ajouté et sera importé dès la fin de son téléchargement.",
                                   DiscordUtils.link(this.getEntity().getName(), this.getEntity().getLink())
                        ))
                           .setColor(Color.ORANGE);
    }

}
