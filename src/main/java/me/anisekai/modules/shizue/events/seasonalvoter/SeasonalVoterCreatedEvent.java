package me.anisekai.modules.shizue.events.seasonalvoter;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.shizue.entities.SeasonalVoter;

public class SeasonalVoterCreatedEvent extends EntityCreatedEvent<SeasonalVoter> {

    public SeasonalVoterCreatedEvent(Object source, SeasonalVoter entity) {

        super(source, entity);
    }

}
