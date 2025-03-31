package me.anisekai.server.events.interest;

import me.anisekai.server.entities.Interest;
import me.anisekai.server.events.InterestUpdatedEvent;

public class InterestLevelUpdatedEvent extends InterestUpdatedEvent<Long> {

    public InterestLevelUpdatedEvent(Object source, Interest entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
