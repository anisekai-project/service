package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.SeasonalVote;
import me.anisekai.modules.shizue.entities.SeasonalVoter;
import me.anisekai.modules.shizue.events.seasonalselection.SeasonalSelectionClosedUpdatedEvent;

import java.util.Set;

public interface ISeasonalSelection extends IEntity<Long> {

    String getName();

    void setName(String name);

    Set<Anime> getAnimes();

    void setAnimes(Set<Anime> animes);

    Set<SeasonalVoter> getVoters();

    void setVoters(Set<SeasonalVoter> voters);

    Set<SeasonalVote> getVotes();

    void setVotes(Set<SeasonalVote> votes);

    boolean isClosed();

    @TriggerEvent(SeasonalSelectionClosedUpdatedEvent.class)
    void setClosed(boolean closed);

}
