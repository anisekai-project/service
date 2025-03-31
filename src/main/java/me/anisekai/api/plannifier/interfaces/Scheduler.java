package me.anisekai.api.plannifier.interfaces;

import me.anisekai.api.plannifier.data.CalibrationResult;
import me.anisekai.api.plannifier.interfaces.entities.Plannifiable;
import me.anisekai.api.plannifier.interfaces.entities.WatchTarget;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Scheduler<T extends WatchTarget, I extends Plannifiable<T>, E extends I> {

    /**
     * Retrieve the {@link SchedulerManager} that will handle entity management with applicative services.
     *
     * @return A {@link SchedulerManager}
     */
    SchedulerManager<T, I, E> getManager();

    // <editor-fold desc="State Queries">

    /**
     * Retrieve the current state for this {@link Scheduler}. The {@link Set} should be immutable.
     *
     * @return A state
     */
    Set<E> getState();

    /**
     * Check in the current state for a {@link Plannifiable} starting right before the provided {@link ZonedDateTime}.
     *
     * @param when
     *         {@link ZonedDateTime} filtering all {@link Plannifiable} possible in the state.
     *
     * @return An optional {@link Plannifiable}.
     */
    Optional<E> findPrevious(ZonedDateTime when);

    /**
     * Check in the current state for a {@link Plannifiable} starting right after the provided {@link ZonedDateTime}.
     *
     * @param when
     *         {@link ZonedDateTime} filtering all {@link Plannifiable} possible in the state.
     *
     * @return An optional {@link Plannifiable}.
     */
    Optional<E> findNext(ZonedDateTime when);

    /**
     * Check in the current state for a {@link Plannifiable} starting right before the provided {@link ZonedDateTime}
     * while matching the provided {@link WatchTarget}.
     *
     * @param when
     *         {@link ZonedDateTime} filtering all {@link Plannifiable} possible in the state.
     * @param target
     *         {@link WatchTarget} further filtering possible {@link Plannifiable}.
     *
     * @return An optional {@link Plannifiable}.
     */
    Optional<E> findPrevious(ZonedDateTime when, T target);

    /**
     * Check in the current state for a {@link Plannifiable} starting right after the provided {@link ZonedDateTime}
     * while matching the provided {@link WatchTarget}.
     *
     * @param when
     *         {@link ZonedDateTime} filtering all plannifiable possible in the state.
     * @param target
     *         {@link WatchTarget} further filtering possible {@link Plannifiable}.
     *
     * @return An optional {@link Plannifiable}.
     */
    Optional<E> findNext(ZonedDateTime when, T target);

    /**
     * Check if the provided {@link ScheduleSpotData} can fit in the current {@link Scheduler}'s state.
     *
     * @param spot
     *         {@link ScheduleSpotData} to use when checking fitness.
     *
     * @return True if the provided {@link ScheduleSpotData} can be scheduled, false otherwise.
     */
    boolean canSchedule(ScheduleSpotData<T> spot);

    /**
     * Schedule the provided {@link ScheduleSpotData} within this {@link Scheduler}. This will automatically update its
     * internal state.
     *
     * @param spot
     *         {@link ScheduleSpotData} to use as source for scheduling data.
     *
     * @return The scheduled entity.
     */
    E schedule(ScheduleSpotData<T> spot);

    // </editor-fold>

    // <editor-fold desc="State Actions">

    /**
     * Delay by the provided amount every {@link Plannifiable} being in the interval. The interval is defined by a
     * starting {@link ZonedDateTime} and a duration.
     *
     * @param from
     *         {@link ZonedDateTime} defining the start of the interval
     * @param interval
     *         {@link Duration} defining the length of the interval
     * @param delay
     *         {@link Duration} defining the length of the delay to apply to every matching {@link Plannifiable}.
     *
     * @return All updated entities.
     */
    List<E> delay(ZonedDateTime from, Duration interval, Duration delay);

    /**
     * Calibrate all {@link E} in this {@link Scheduler}. Calibration will reprocess every episode count and remove
     * superfluous events. This won't merge existing event.
     *
     * @return A {@link CalibrationResult} containing update/delete count.
     */
    CalibrationResult calibrate();

    // </editor-fold>

}
