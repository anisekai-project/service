package me.anisekai.modules.shizue.events.interest;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.shizue.entities.Interest;

public class InterestCreatedEvent extends EntityCreatedEvent<Interest> {

    public InterestCreatedEvent(Object source, Interest entity) {

        super(source, entity);
    }

}
