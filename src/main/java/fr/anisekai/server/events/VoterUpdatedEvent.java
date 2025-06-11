package fr.anisekai.server.events;

import fr.anisekai.server.entities.DiscordUser;

public class VoterUpdatedEvent<V> extends EntityUpdatedEventAdapter<DiscordUser, V> {

    public VoterUpdatedEvent(Object source, DiscordUser entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
