package fr.anisekai.server.events.broadcast;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.events.BroadcastUpdatedEvent;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;

public class BroadcastStatusUpdatedEvent extends BroadcastUpdatedEvent<BroadcastStatus> {

    public BroadcastStatusUpdatedEvent(Object source, Broadcast entity, BroadcastStatus previous, BroadcastStatus current) {

        super(source, entity, previous, current);
    }

}
