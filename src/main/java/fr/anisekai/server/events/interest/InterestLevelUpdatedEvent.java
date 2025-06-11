package fr.anisekai.server.events.interest;

import fr.anisekai.server.entities.Interest;
import fr.anisekai.server.events.InterestUpdatedEvent;

public class InterestLevelUpdatedEvent extends InterestUpdatedEvent<Byte> {

    public InterestLevelUpdatedEvent(Object source, Interest entity, Byte previous, Byte current) {

        super(source, entity, previous, current);
    }

}
