package me.anisekai.modules.chiya.events.user;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;

public class UserEmoteUpdatedEvent extends EntityUpdatedEvent<DiscordUser, String> {

    public UserEmoteUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
