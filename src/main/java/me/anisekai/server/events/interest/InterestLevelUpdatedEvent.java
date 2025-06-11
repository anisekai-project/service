package me.anisekai.server.events.interest;

import me.anisekai.server.entities.Interest;
import me.anisekai.server.events.InterestUpdatedEvent;

public class InterestLevelUpdatedEvent extends InterestUpdatedEvent<Byte> {

    public InterestLevelUpdatedEvent(Object source, Interest entity, Byte previous, Byte current) {

        super(source, entity, previous, current);
    }

}
