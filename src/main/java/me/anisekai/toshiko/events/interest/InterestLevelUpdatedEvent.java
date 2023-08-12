package me.anisekai.toshiko.events.interest;

import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class InterestLevelUpdatedEvent extends EntityUpdatedEvent<Interest, InterestLevel> {

    public InterestLevelUpdatedEvent(Object source, Interest entity, InterestLevel previous, InterestLevel current) {

        super(source, entity, previous, current);
    }

}
