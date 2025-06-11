package fr.anisekai.server.events.user;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.UserUpdatedEvent;

public class UserAdministratorUpdatedEvent extends UserUpdatedEvent<Boolean> {

    public UserAdministratorUpdatedEvent(Object source, DiscordUser entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
