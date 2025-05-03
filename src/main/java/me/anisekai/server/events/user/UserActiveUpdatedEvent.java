package me.anisekai.server.events.user;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.UserUpdatedEvent;

public class UserActiveUpdatedEvent extends UserUpdatedEvent<Boolean> {

    public UserActiveUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
