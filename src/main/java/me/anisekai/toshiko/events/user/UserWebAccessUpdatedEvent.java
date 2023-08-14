package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class UserWebAccessUpdatedEvent extends EntityUpdatedEvent<DiscordUser, Boolean> {

    public UserWebAccessUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
