package me.anisekai.modules.shizue.events.seasonalselection;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.shizue.entities.SeasonalSelection;

public class SeasonalSelectionCreated extends EntityCreatedEvent<SeasonalSelection> {

    public SeasonalSelectionCreated(Object source, SeasonalSelection entity) {

        super(source, entity);
    }

}
