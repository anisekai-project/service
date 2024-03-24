package me.anisekai.modules.chiya.events.user;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;

public class UserDiscriminatorUpdatedEvent extends EntityUpdatedEvent<DiscordUser, String> {

    public UserDiscriminatorUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
