package me.anisekai.modules.shizue.events.seasonalvote;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.shizue.entities.SeasonalVote;

public class SeasonalVoteCreatedEvent extends EntityCreatedEvent<SeasonalVote> {

    public SeasonalVoteCreatedEvent(Object source, SeasonalVote entity) {

        super(source, entity);
    }

}
