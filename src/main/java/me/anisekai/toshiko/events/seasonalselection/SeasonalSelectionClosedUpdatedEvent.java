package me.anisekai.toshiko.events.seasonalselection;

import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class SeasonalSelectionClosedUpdatedEvent extends EntityUpdatedEvent<SeasonalSelection, Boolean> {

    public SeasonalSelectionClosedUpdatedEvent(Object source, SeasonalSelection entity, Boolean previous, Boolean current) {

        super(source, entity, previous, current);
    }

}
