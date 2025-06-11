package fr.anisekai.server.events.voter;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.VoterUpdatedEvent;

public class VoterAmountUpdatedEvent extends VoterUpdatedEvent<Short> {

    public VoterAmountUpdatedEvent(Object source, DiscordUser entity, Short previous, Short current) {

        super(source, entity, previous, current);
    }

}
