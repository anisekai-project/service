package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.EntityUpdatedEvent;
import me.anisekai.toshiko.interfaces.LoggableEvent;
import me.anisekai.toshiko.utils.Embedding;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AnimeTotalUpdatedEvent extends EntityUpdatedEvent<Anime, Long> implements LoggableEvent {

    public AnimeTotalUpdatedEvent(Object source, Anime entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return Embedding.event(this)
                .setDescription(Embedding.link(this.getEntity()))
                .addField(
                        "Changement de status",
                        String.format("**%s** ► **%s**", this.getPrevious(), this.getCurrent()),
                        false
                ).setColor(Color.WHITE);
    }

}
