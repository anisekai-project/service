package me.anisekai.toshiko.events.watchlist;

import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.CronState;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class WatchlistStateUpdatedEvent extends EntityUpdatedEvent<Watchlist, CronState> {

    public WatchlistStateUpdatedEvent(Object source, Watchlist entity, CronState previous, CronState current) {

        super(source, entity, previous, current);
    }

}
