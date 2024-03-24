package me.anisekai.modules.chiya.events.user;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;

public class UserCreatedEvent extends EntityCreatedEvent<DiscordUser> {

    public UserCreatedEvent(Object source, DiscordUser entity) {

        super(source, entity);
    }

}
