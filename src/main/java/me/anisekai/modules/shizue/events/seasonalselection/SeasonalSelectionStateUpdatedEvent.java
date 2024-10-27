package me.anisekai.modules.shizue.events.seasonalselection;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.enums.SeasonalSelectionState;

public class SeasonalSelectionStateUpdatedEvent extends EntityUpdatedEvent<SeasonalSelection, SeasonalSelectionState> {

    public SeasonalSelectionStateUpdatedEvent(Object source, SeasonalSelection entity, SeasonalSelectionState previous, SeasonalSelectionState current) {

        super(source, entity, previous, current);
    }

}
