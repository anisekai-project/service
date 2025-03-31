package me.anisekai.server.events.selection;

import me.anisekai.server.entities.Selection;
import me.anisekai.server.enums.Season;
import me.anisekai.server.events.SelectionUpdatedEvent;

public class SelectionSeasonUpdatedEvent extends SelectionUpdatedEvent<Season> {

    public SelectionSeasonUpdatedEvent(Object source, Selection entity, Season previous, Season current) {

        super(source, entity, previous, current);
    }

}
