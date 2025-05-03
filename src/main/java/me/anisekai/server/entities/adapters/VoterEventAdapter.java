package me.anisekai.server.entities.adapters;

import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.interfaces.VoterEntity;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.events.voter.VoterAmountUpdatedEvent;
import me.anisekai.server.events.voter.VoterVotesUpdatedEvent;
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
