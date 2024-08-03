package me.anisekai.api.plannifier.interfaces;

import me.anisekai.globals.exceptions.SilentDiscordException;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface Scheduler<I extends Plannifiable, E extends I> {

    /**
     * Find the previous scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link ZonedDateTime}.
     *
     * @param dateTime
     *         The {@link ZonedDateTime} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    Optional<E> findPrevious(ZonedDateTime dateTime);

    /**
     * Find the next scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link ZonedDateTime}.
     *
     * @param dateTime
     *         The {@link ZonedDateTime} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    Optional<E> findNext(ZonedDateTime dateTime);
    /**
     * Find the previous scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link Plannifiable}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    Optional<E> findPrevious(I plannifiable);

    /**
     * Find the next scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link Plannifiable}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    Optional<E> findNext(I plannifiable);

    /**
     * Check if the provided {@link Plannifiable} can fit in this {@link Scheduler}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to check against this {@link Scheduler}.
     *
     * @return True if {@link #schedule(Plannifiable)} can be safely called, false otherwise.
     */
    boolean canSchedule(I plannifiable);

    /**
     * Schedule the provided {@link Plannifiable} within this {@link Scheduler}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to schedule.
     *
     * @return The {@link Plannifiable} entity scheduled.
     *
     * @throws SilentDiscordException Threw if the scheduling fails at some point.
     */
    E schedule(I plannifiable);

}
