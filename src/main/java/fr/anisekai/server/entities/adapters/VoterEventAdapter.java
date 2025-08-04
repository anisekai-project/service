package fr.anisekai.server.entities.adapters;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.events.voter.VoterAmountUpdatedEvent;
import fr.anisekai.server.events.voter.VoterVotesUpdatedEvent;
import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.interfaces.VoterEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface VoterEventAdapter extends VoterEntity<Selection, DiscordUser, Anime> {

    @Override
    @TriggerEvent(VoterAmountUpdatedEvent.class)
    void setAmount(short amount);

    @Override
    @TriggerEvent(VoterVotesUpdatedEvent.class)
    void setVotes(@NotNull Set<Anime> votes);

}
