package me.anisekai.toshiko.events.seasonalselection;

import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class SeasonalSelectionCreated extends EntityCreatedEvent<SeasonalSelection> {

    public SeasonalSelectionCreated(Object source, SeasonalSelection entity) {

        super(source, entity);
    }

}
