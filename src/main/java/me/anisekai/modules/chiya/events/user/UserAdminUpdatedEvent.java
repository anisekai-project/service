package me.anisekai.modules.chiya.events.user;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;

public class UserAdminUpdatedEvent extends EntityUpdatedEvent<DiscordUser, Boolean> {

    public UserAdminUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
