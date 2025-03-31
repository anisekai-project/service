package me.anisekai.server.events.voter;

import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.VoterUpdatedEvent;

public class VoterAmountUpdatedEvent extends VoterUpdatedEvent<Long> {

    public VoterAmountUpdatedEvent(Object source, DiscordUser entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
