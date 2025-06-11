package me.anisekai.server.events;

import me.anisekai.server.entities.DiscordUser;

public class UserUpdatedEvent<V> extends EntityUpdatedEventAdapter<DiscordUser, V> {

    public UserUpdatedEvent(Object source, DiscordUser entity, V oldValue, V newValue) {

        super(source, entity, oldValue, newValue);
    }

}
