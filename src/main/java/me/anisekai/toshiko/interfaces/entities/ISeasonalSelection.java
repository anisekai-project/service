package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.events.seasonalselection.SeasonalSelectionClosedUpdatedEvent;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.helpers.proxy.TriggerEvent;

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
