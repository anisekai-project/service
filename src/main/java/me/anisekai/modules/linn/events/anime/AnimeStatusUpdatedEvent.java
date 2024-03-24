package me.anisekai.modules.linn.events.anime;

import me.anisekai.globals.events.LoggableEvent;
import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.globals.utils.Embedding;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AnimeStatusUpdatedEvent extends EntityUpdatedEvent<Anime, AnimeStatus> implements LoggableEvent {

    public AnimeStatusUpdatedEvent(Object source, Anime entity, AnimeStatus previous, AnimeStatus current) {

        super(source, entity, previous, current);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return Embedding.event(this)
                        .setDescription(Embedding.link(this.getEntity()))
                        .addField(
                                "Changement de status",
                                String.format(
                                        "**%s** ► **%s**",
                                        this.getPrevious().getDisplay(),
                                        this.getCurrent().getDisplay()
                                ),
                                false
                        ).setColor(Color.WHITE);
    }

}
