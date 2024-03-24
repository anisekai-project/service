package me.anisekai.modules.chiya.events.user;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;

public class UserWebAccessUpdatedEvent extends EntityUpdatedEvent<DiscordUser, Boolean> {

    public UserWebAccessUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
