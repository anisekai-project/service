package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class UserEmoteUpdatedEvent extends EntityUpdatedEvent<DiscordUser, String> {

    public UserEmoteUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
