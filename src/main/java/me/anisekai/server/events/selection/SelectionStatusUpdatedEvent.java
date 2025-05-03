package me.anisekai.server.events.selection;

import fr.anisekai.wireless.remote.enums.SelectionStatus;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.events.SelectionUpdatedEvent;

public class SelectionStatusUpdatedEvent extends SelectionUpdatedEvent<SelectionStatus> {

    public SelectionStatusUpdatedEvent(Object source, Selection entity, SelectionStatus previous, SelectionStatus current) {

        super(source, entity, previous, current);
    }

}
