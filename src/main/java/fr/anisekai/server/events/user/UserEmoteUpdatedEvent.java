package fr.anisekai.server.events.user;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.UserUpdatedEvent;

public class UserEmoteUpdatedEvent extends UserUpdatedEvent<String> {

    public UserEmoteUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
