package fr.anisekai.server.events.user;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.UserUpdatedEvent;

public class UserUsernameUpdatedEvent extends UserUpdatedEvent<String> {

    public UserUsernameUpdatedEvent(Object source, DiscordUser entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
