package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityUpdatedEvent;
import me.anisekai.toshiko.interfaces.LoggableEvent;
import me.anisekai.toshiko.utils.Embedding;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class UserUsernameUpdatedEvent extends EntityUpdatedEvent<DiscordUser, String> implements LoggableEvent {

    public UserUsernameUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

    @Override
    public EmbedBuilder asEmbed() {

        return Embedding.event(this)
                        .setDescription("Un utilisateur a été mis à jour")
                        .addField(
                                "Changement de nom",
                                String.format("**%s** ► **%s**", this.getPrevious(), this.getCurrent()),
                                false
                        ).setColor(Color.CYAN);
    }


}
