package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.enums.Season;
import me.anisekai.server.enums.SelectionStatus;
import me.anisekai.server.events.selection.SelectionAnimesUpdatedEvent;
import me.anisekai.server.events.selection.SelectionSeasonUpdatedEvent;
import me.anisekai.server.events.selection.SelectionYearUpdatedEvent;

import java.util.Set;

/**
 * Interface representing an object holding data about a seasonal selection.
 */
public interface ISelection<C extends IAnime<?>> extends IEntity<Long> {

    /**
     * Retrieve this {@link ISelection}'s season.
     *
     * @return A {@link Season}.
     */
    Season getSeason();

    /**
     * Define this {@link ISelection}'s season.
     *
     * @param season
     *         A {@link Season}.
     */
    @TriggerEvent(SelectionSeasonUpdatedEvent.class)
    void setSeason(Season season);

    /**
     * Retrieve this {@link ISelection}'s year.
     *
     * @return A year
     */
    int getYear();

    /**
     * Define this {@link ISelection}'s year.
     *
     * @param year
     *         A year
     */
    @TriggerEvent(SelectionYearUpdatedEvent.class)
    void setYear(int year);

    /**
     * Retrieve this {@link ISelection}'s status.
     *
     * @return A {@link SelectionStatus}.
     */
    SelectionStatus getStatus();

    /**
     * Define this {@link ISelection}'s status.
     *
     * @param status
     *         A {@link SelectionStatus}.
     */
    void setStatus(SelectionStatus status);

    /**
     * Retrieve all {@link IAnime} being part of this {@link ISelection}. Those are the {@link IAnime} that can receive
     * votes while the selection is open.
     *
     * @return A {@link Set} of {@link IAnime}.
     */
    Set<C> getAnimes();

    /**
     * Define all {@link IAnime} being part of this {@link ISelection}. Those are the {@link IAnime} that can receive
     * votes while the selection is open.
     *
     * @param animes
     *         A {@link Set} of {@link IAnime}.
     */
    @TriggerEvent(SelectionAnimesUpdatedEvent.class)
    void setAnimes(Set<C> animes);

    /**
     * Retrieve this {@link ISelection} label which, by default, joins the {@link Season}'s label and the year.
     *
     * @return A label
     */
    default String getLabel() {

        return String.format("%s %s", this.getSeason().getLabel(), this.getYear());
    }

}
