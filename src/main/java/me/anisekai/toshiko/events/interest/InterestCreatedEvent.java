package me.anisekai.toshiko.events.interest;

import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class InterestCreatedEvent extends EntityCreatedEvent<Interest> {

    public InterestCreatedEvent(Object source, Interest entity) {

        super(source, entity);
    }

}
