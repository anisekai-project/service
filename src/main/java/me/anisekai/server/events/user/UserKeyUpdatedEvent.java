package me.anisekai.server.events.user;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.UserUpdatedEvent;

public class UserKeyUpdatedEvent extends UserUpdatedEvent<String> {

    public UserKeyUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
