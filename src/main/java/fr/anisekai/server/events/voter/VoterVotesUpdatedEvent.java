package fr.anisekai.server.events.voter;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.VoterUpdatedEvent;

import java.util.Set;

public class VoterVotesUpdatedEvent extends VoterUpdatedEvent<Set<Anime>> {

    public VoterVotesUpdatedEvent(Object source, DiscordUser entity, Set<Anime> previous, Set<Anime> current) {

        super(source, entity, previous, current);
    }

}
