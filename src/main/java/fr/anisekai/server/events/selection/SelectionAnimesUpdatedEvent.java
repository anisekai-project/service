package fr.anisekai.server.events.selection;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.events.SelectionUpdatedEvent;

import java.util.Set;

public class SelectionAnimesUpdatedEvent extends SelectionUpdatedEvent<Set<Anime>> {

    public SelectionAnimesUpdatedEvent(Object source, Selection entity, Set<Anime> previous, Set<Anime> current) {

        super(source, entity, previous, current);
    }

}
