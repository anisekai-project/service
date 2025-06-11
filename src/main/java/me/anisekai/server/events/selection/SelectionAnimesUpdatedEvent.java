package me.anisekai.server.events.selection;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.events.SelectionUpdatedEvent;

import java.util.Set;

public class SelectionAnimesUpdatedEvent extends SelectionUpdatedEvent<Set<Anime>> {

    public SelectionAnimesUpdatedEvent(Object source, Selection entity, Set<Anime> previous, Set<Anime> current) {

        super(source, entity, previous, current);
    }

}
