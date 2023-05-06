package me.anisekai.toshiko.events.selections;

import me.anisekai.toshiko.entities.SeasonalSelection;
import org.springframework.context.ApplicationEvent;

public class SeasonalSelectionClosedEvent extends ApplicationEvent {

    private final SeasonalSelection seasonalSelection;

    public SeasonalSelectionClosedEvent(Object source, SeasonalSelection seasonalSelection) {

        super(source);
        this.seasonalSelection = seasonalSelection;
    }

    public SeasonalSelection getSeasonalSelection() {

        return this.seasonalSelection;
    }
}
