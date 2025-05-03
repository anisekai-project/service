package me.anisekai.server.events.voter;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.VoterUpdatedEvent;

public class VoterAmountUpdatedEvent extends VoterUpdatedEvent<Short> {

    public VoterAmountUpdatedEvent(Object source, DiscordUser entity, Short previous, Short current) {

        super(source, entity, previous, current);
    }

}
