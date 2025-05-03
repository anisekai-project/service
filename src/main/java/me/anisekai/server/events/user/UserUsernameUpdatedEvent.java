package me.anisekai.server.events.user;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.UserUpdatedEvent;

public class UserUsernameUpdatedEvent extends UserUpdatedEvent<String> {

    public UserUsernameUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
