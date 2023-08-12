package me.anisekai.toshiko.events.seasonalvote;

import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class SeasonalVoteCreatedEvent extends EntityCreatedEvent<SeasonalVote> {

    public SeasonalVoteCreatedEvent(Object source, SeasonalVote entity) {

        super(source, entity);
    }

}
