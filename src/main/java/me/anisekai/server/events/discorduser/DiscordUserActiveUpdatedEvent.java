package me.anisekai.server.events.discorduser;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.DiscordUserUpdatedEvent;

public class DiscordUserActiveUpdatedEvent extends DiscordUserUpdatedEvent<Boolean> {

    public DiscordUserActiveUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
