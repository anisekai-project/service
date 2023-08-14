package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class UserAdminUpdatedEvent extends EntityUpdatedEvent<DiscordUser, Boolean> {

    public UserAdminUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
