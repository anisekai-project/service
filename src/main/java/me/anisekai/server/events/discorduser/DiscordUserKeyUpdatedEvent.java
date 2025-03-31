package me.anisekai.server.events.discorduser;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.DiscordUserUpdatedEvent;

public class DiscordUserKeyUpdatedEvent extends DiscordUserUpdatedEvent<String> {

    public DiscordUserKeyUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
