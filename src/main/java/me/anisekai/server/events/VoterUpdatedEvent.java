package me.anisekai.server.events;

import me.anisekai.server.entities.DiscordUser;

public class VoterUpdatedEvent<V> extends EntityUpdatedEventAdapter<DiscordUser, V> {

    public VoterUpdatedEvent(Object source, DiscordUser entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
