package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class UserActiveUpdatedEvent extends EntityUpdatedEvent<DiscordUser, Boolean> {

    public UserActiveUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
