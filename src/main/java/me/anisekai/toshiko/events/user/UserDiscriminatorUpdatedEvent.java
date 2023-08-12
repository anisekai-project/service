package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class UserDiscriminatorUpdatedEvent extends EntityUpdatedEvent<DiscordUser, String> {

    public UserDiscriminatorUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
