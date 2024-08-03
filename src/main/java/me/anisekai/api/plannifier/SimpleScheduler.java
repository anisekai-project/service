package me.anisekai.api.plannifier;

import me.anisekai.api.plannifier.exceptions.InvalidSchedulingDurationException;
import me.anisekai.api.plannifier.exceptions.NotSchedulableException;
import me.anisekai.api.plannifier.exceptions.SchedulingCreationException;
import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.PlannificationManager;
import me.anisekai.api.plannifier.interfaces.Scheduler;
import me.anisekai.globals.exceptions.SilentDiscordException;
import me.anisekai.globals.utils.DateTimeUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;

public class SimpleScheduler<I extends Plannifiable, E extends I> implements Scheduler<I, E> {

    public static final BiFunction<Plannifiable, Plannifiable, Boolean> IS_OVERLAPPING_FUNCTION = (item, found) -> {

        ZonedDateTime startingAt = item.getStartingAt();
        ZonedDateTime endingAt   = startingAt.plus(item.getDuration());

        ZonedDateTime itemStartingAt = found.getStartingAt();
        ZonedDateTime itemEndingAt   = itemStartingAt.plus(found.getDuration());

        return !startingAt.isAfter(itemEndingAt)
                && !startingAt.equals(itemEndingAt)
                && !endingAt.isBefore(itemStartingAt)
                && !endingAt.isEqual(itemStartingAt);
    };

    private final PlannificationManager<I, E> manager;
    private final Set<E>                   state;

    public SimpleScheduler(PlannificationManager<I, E> manager) {

        this.manager = manager;
        this.state   = new HashSet<>();
    }

    public SimpleScheduler(Collection<E> initialItems, PlannificationManager<I, E> manager) {

        this(manager);
        this.state.addAll(initialItems);
    }

    Set<E> getState() {

        return this.state;
    }

    PlannificationManager<I, E> getManager() {

        return this.manager;
    }

    /**
     * Find the previous scheduled {@link E} in this {@link Scheduler} relative to the provided {@link ZonedDateTime}.
     *
     * @param dateTime
     *         The {@link ZonedDateTime} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    @Override
    public Optional<E> findPrevious(ZonedDateTime dateTime) {

        return this.state.stream()
                         .filter(item -> DateTimeUtils.isBeforeOrEquals(item.getStartingAt(), dateTime))
                         .max(Comparator.comparing(Plannifiable::getStartingAt));
    }

    /**
     * Find the next scheduled {@link E} in this {@link Scheduler} relative to the provided {@link ZonedDateTime}.
     *
     * @param dateTime
     *         The {@link ZonedDateTime} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    @Override
    public Optional<E> findNext(ZonedDateTime dateTime) {

        return this.state.stream()
                         .filter(item -> DateTimeUtils.isAfterOrEquals(item.getStartingAt(), dateTime))
                         .min(Comparator.comparing(Plannifiable::getStartingAt));
    }

    /**
     * Find the direct previous scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link Plannifiable}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    @Override
    public Optional<E> findPrevious(I plannifiable) {

        return this.findPrevious(plannifiable.getStartingAt());
    }

    /**
     * Find the direct next scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link Plannifiable}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to which the other scheduled event will be tested against.
     *
     * @return A {@link Plannifiable}, if any has been found.
     */
    @Override
    public Optional<E> findNext(I plannifiable) {

        return this.findNext(plannifiable.getStartingAt());
    }

    /**
     * Check if the provided {@link Plannifiable} can fit in this {@link Scheduler}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to check against this {@link Scheduler}.
     *
     * @return True if {@link #schedule(Plannifiable)} can be safely called, false otherwise.
     */
    @Override
    public boolean canSchedule(I plannifiable) {

        Duration duration = plannifiable.getDuration();

        if (duration.isNegative() || duration.isZero()) {
            throw new InvalidSchedulingDurationException();
        }

        // Only select previous and next then check for overlaps.

        boolean previousOverlap = this.findPrevious(plannifiable)
                                      .filter(item -> IS_OVERLAPPING_FUNCTION.apply(plannifiable, item))
                                      .isPresent();

        boolean nextOverlap = this.findNext(plannifiable)
                                  .filter(item -> IS_OVERLAPPING_FUNCTION.apply(plannifiable, item))
                                  .isPresent();

        return !previousOverlap && !nextOverlap;
    }

    /**
     * Schedule the provided {@link Plannifiable} within this {@link Scheduler}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} to schedule.
     *
     * @return The {@link Plannifiable} entity scheduled.
     *
     * @throws SilentDiscordException
     *         Threw if the scheduling fails at some point.
     */
    @Override
    public E schedule(I plannifiable) {

        if (this.canSchedule(plannifiable)) {
            try {
                E result = this.manager.requestCreate(plannifiable);
                this.state.add(result);
                return result;
            } catch (Exception e) {
                throw new SchedulingCreationException(e);
            }
        }

        throw new NotSchedulableException();
    }

}
