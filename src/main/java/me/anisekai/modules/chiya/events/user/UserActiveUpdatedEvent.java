package me.anisekai.modules.chiya.events.user;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;

public class UserActiveUpdatedEvent extends EntityUpdatedEvent<DiscordUser, Boolean> {

    public UserActiveUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
