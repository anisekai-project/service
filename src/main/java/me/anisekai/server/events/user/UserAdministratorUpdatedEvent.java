package me.anisekai.server.events.user;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.UserUpdatedEvent;

public class UserAdministratorUpdatedEvent extends UserUpdatedEvent<Boolean> {

    public UserAdministratorUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
