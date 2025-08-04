package fr.anisekai.server.events.selection;

import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.events.SelectionUpdatedEvent;
import fr.anisekai.wireless.remote.enums.SelectionStatus;

public class SelectionStatusUpdatedEvent extends SelectionUpdatedEvent<SelectionStatus> {

    public SelectionStatusUpdatedEvent(Object source, Selection entity, SelectionStatus previous, SelectionStatus current) {

        super(source, entity, previous, current);
    }

}
