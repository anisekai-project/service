package me.anisekai.server.events.voter;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.events.VoterUpdatedEvent;

import java.util.Set;

public class VoterVotesUpdatedEvent extends VoterUpdatedEvent<Set<Anime>> {

    public VoterVotesUpdatedEvent(Object source, DiscordUser entity, Set<Anime> previous, Set<Anime> current) {

        super(source, entity, previous, current);
    }

}
