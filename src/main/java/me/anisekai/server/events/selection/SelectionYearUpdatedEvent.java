package me.anisekai.server.events.selection;

import me.anisekai.server.entities.Selection;
import me.anisekai.server.events.SelectionUpdatedEvent;

public class SelectionYearUpdatedEvent extends SelectionUpdatedEvent<Integer> {

    public SelectionYearUpdatedEvent(Object source, Selection entity, Integer previous, Integer current) {

        super(source, entity, previous, current);
    }

}
