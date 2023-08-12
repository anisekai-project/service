package me.anisekai.toshiko.events.seasonalvoter;

import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class SeasonalVoterCreatedEvent extends EntityCreatedEvent<SeasonalVoter> {

    public SeasonalVoterCreatedEvent(Object source, SeasonalVoter entity) {

        super(source, entity);
    }

}
