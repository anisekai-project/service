package me.anisekai.modules.shizue.events.seasonalselection;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.shizue.entities.SeasonalSelection;

public class SeasonalSelectionClosedUpdatedEvent extends EntityUpdatedEvent<SeasonalSelection, Boolean> {

    public SeasonalSelectionClosedUpdatedEvent(Object source, SeasonalSelection entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
