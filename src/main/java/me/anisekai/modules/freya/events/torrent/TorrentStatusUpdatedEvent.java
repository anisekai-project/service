package me.anisekai.modules.freya.events.torrent;

import me.anisekai.globals.events.LoggableEvent;
import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.freya.entities.Torrent;
import me.anisekai.modules.freya.enums.TorrentStatus;
import me.anisekai.globals.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TorrentStatusUpdatedEvent extends EntityUpdatedEvent<Torrent, TorrentStatus> implements LoggableEvent {

    public TorrentStatusUpdatedEvent(Object source, Torrent entity, TorrentStatus previous, TorrentStatus current) {

        super(source, entity, previous, current);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return DiscordUtils.event(this)
                           .setDescription(String.format(
                                   "Le torrent %s a été mis à jour",
                                   DiscordUtils.link(this.getEntity().getName(), this.getEntity().getLink())
                        ))
                           .addField(
                                "Changement de status",
                                String.format("**%s** ► **%s**", this.getPrevious().name(), this.getCurrent().name()),
                                false
                        ).setColor(Color.WHITE);
    }

}
