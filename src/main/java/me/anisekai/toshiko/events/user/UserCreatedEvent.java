package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class UserCreatedEvent extends EntityCreatedEvent<DiscordUser> {

    public UserCreatedEvent(Object source, DiscordUser entity) {

        super(source, entity);
    }

}
