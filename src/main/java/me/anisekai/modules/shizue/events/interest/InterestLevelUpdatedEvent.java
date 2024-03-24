package me.anisekai.modules.shizue.events.interest;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.Interest;
import me.anisekai.modules.shizue.enums.InterestLevel;

public class InterestLevelUpdatedEvent extends EntityUpdatedEvent<Interest, InterestLevel> {

    public InterestLevelUpdatedEvent(Object source, Interest entity, InterestLevel previous, InterestLevel current) {

        super(source, entity, previous, current);
    }

}
