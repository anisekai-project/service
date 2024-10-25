package me.anisekai.api.plannifier;

import me.anisekai.api.plannifier.data.BookedPlannifiable;
import me.anisekai.api.plannifier.data.CalibrationResult;
import me.anisekai.api.plannifier.exceptions.DelayOverlapException;
import me.anisekai.api.plannifier.exceptions.InvalidSchedulingDurationException;
import me.anisekai.api.plannifier.exceptions.NotSchedulableException;
import me.anisekai.api.plannifier.interfaces.*;
import me.anisekai.globals.utils.DateTimeUtils;
import me.anisekai.globals.utils.collectors.MapCollector;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class allowing easy management of a schedule.
 *
 * @param <T>
 *         The watch target type. A watch target is some sort of container, representing a movie or series.
 * @param <E>
 *         The entity type. It is the type that will be scheduled.
 */
public class EventScheduler<T extends WatchTarget, I extends Plannifiable<T>, E extends I> implements Scheduler<T, I, E> {

    /**
     * If two {@link Plannifiable} are of the following duration apart, they will be merged once scheduled. The duration
     * is done by comparing one {@link Plannifiable#getStartingAt()} with the endpoint of another {@link Plannifiable},
     * being {@link Plannifiable#getStartingAt()} + {@link Plannifiable#getDuration()}.
     */
    private static final Duration MERGE_MAGNET_LIMIT = Duration.ofMinutes(10);

    /**
     * Check if provided {@link ScheduleSpotData} overlap one another.
     *
     * @param one
     *         The first {@link ScheduleSpotData}
     * @param two
     *         The second {@link ScheduleSpotData}
     *
     * @return True if the {@link ScheduleSpotData} overlaps, false otherwise.
     */
    private boolean isOverlapping(ScheduleSpotData<T> one, ScheduleSpotData<T> two) {

        ZonedDateTime startingAt = one.getStartingAt();
        ZonedDateTime endingAt   = startingAt.plus(one.getDuration());

        ZonedDateTime itemStartingAt = two.getStartingAt();
        ZonedDateTime itemEndingAt   = itemStartingAt.plus(two.getDuration());

        return !startingAt.isAfter(itemEndingAt) && !startingAt.equals(itemEndingAt) && !endingAt.isBefore(
                itemStartingAt) && !endingAt.isEqual(itemStartingAt);
    }

    /**
     * Create a {@link Stream} of the current {@link EventScheduler} state, where every item will be filtered based on
     * the return value of {@link ScheduleSpotData#getStartingAt()}. If the returned value is before or equals to the
     * provided {@link ZonedDateTime}, the item will be kept.
     *
     * @param when
     *         The {@link ZonedDateTime} delimiting item filtering.
     *
     * @return A filtered {@link Stream} of the current state.
     */
    private Stream<E> findPreviousQuery(ZonedDateTime when) {

        return this.getState().stream().filter(item -> DateTimeUtils.isBeforeOrEquals(item.getStartingAt(), when));
    }

    /**
     * Create a {@link Stream} of the current {@link EventScheduler} state, where every item will be filtered based on
     * the return value of {@link ScheduleSpotData#getStartingAt()}. If the returned value is after or equals to the
     * provided {@link ZonedDateTime}, the item will be kept.
     *
     * @param when
     *         The {@link ZonedDateTime} delimiting item filtering.
     *
     * @return A filtered {@link Stream} of the current state.
     */
    private Stream<E> findAfterQuery(ZonedDateTime when) {

        return this.getState().stream().filter(item -> DateTimeUtils.isAfterOrEquals(item.getStartingAt(), when));
    }

    private final SchedulerManager<T, I, E> manager;
    private final Set<E>                 state;

    /**
     * Create a new instance of {@link Scheduler} using the provided {@link SchedulerManager}.
     *
     * @param manager
     *         {@link SchedulerManager} that this {@link Scheduler} will use when using CRUD operations.
     * @param items
     *         Default collection of {@link Plannifiable} that will populate the state.
     */
    public EventScheduler(SchedulerManager<T, I, E> manager, Collection<E> items) {

        this.manager = manager;
        this.state   = new HashSet<>(items);
    }

    /**
     * Retrieve the {@link SchedulerManager} that will handle entity management with applicative services.
     *
     * @return A {@link SchedulerManager}
     */
    @Override
    public SchedulerManager<T, I, E> getManager() {

        return this.manager;
    }

    /**
     * Retrieve the current state for this {@link Scheduler}. The {@link Set} should be immutable.
     *
     * @return A state
     */
    @Override
    public Set<E> getState() {

        return Collections.unmodifiableSet(this.state);
    }

    /**
     * Check in the current state for a {@link Plannifiable} starting right before the provided {@link ZonedDateTime}.
     *
     * @param when
     *         {@link ZonedDateTime} filtering all {@link Plannifiable} possible in the state.
     *
     * @return An optional {@link Plannifiable}.
     */
    @Override
    public Optional<E> findPrevious(ZonedDateTime when) {

        return this.findPreviousQuery(when).max(Comparator.comparing(Plannifiable::getStartingAt));
    }

    /**
     * Check in the current state for a {@link Plannifiable} starting right after the provided {@link ZonedDateTime}.
     *
     * @param when
     *         {@link ZonedDateTime} filtering all {@link Plannifiable} possible in the state.
     *
     * @return An optional {@link Plannifiable}.
     */
    @Override
    public Optional<E> findNext(ZonedDateTime when) {

        return this.findAfterQuery(when).min(Comparator.comparing(Plannifiable::getStartingAt));
    }

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
    @Override
    public Optional<E> findPrevious(ZonedDateTime when, T target) {

        return this.findPreviousQuery(when)
                   .filter(item -> item.getWatchTarget().equals(target))
                   .max(Comparator.comparing(Plannifiable::getStartingAt));
    }

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
    @Override
    public Optional<E> findNext(ZonedDateTime when, T target) {

        return this.findAfterQuery(when)
                   .filter(item -> item.getWatchTarget().equals(target))
                   .min(Comparator.comparing(Plannifiable::getStartingAt));
    }

    /**
     * Check if the provided {@link ScheduleSpotData} can fit in the current {@link Scheduler}'s state.
     *
     * @param spot
     *         {@link ScheduleSpotData} to use when checking fitness.
     *
     * @return True if the provided {@link ScheduleSpotData} can be scheduled, false otherwise.
     */
    @Override
    public boolean canSchedule(ScheduleSpotData<T> spot) {

        Duration duration = spot.getDuration();

        if (duration.isNegative() || duration.isZero()) throw new InvalidSchedulingDurationException();

        boolean prevOverlap = this.findPrevious(spot.getStartingAt())
                                  .map(item -> this.isOverlapping(spot, item))
                                  .orElse(false);

        boolean nextOverlap = this.findNext(spot.getStartingAt())
                                  .map(item -> this.isOverlapping(spot, item))
                                  .orElse(false);

        return !prevOverlap && !nextOverlap;
    }

    /**
     * Schedule the provided {@link ScheduleSpotData} within this {@link Scheduler}. This will automatically update its
     * internal state.
     *
     * @param spot
     *         {@link ScheduleSpotData} to use as source for scheduling data.
     *
     * @return The scheduled entity.
     */
    @Override
    public E schedule(ScheduleSpotData<T> spot) {

        if (!this.canSchedule(spot)) {
            throw new NotSchedulableException();
        }

        Optional<E> optPrev       = this.findPrevious(spot.getStartingAt());
        Optional<E> optNext       = this.findNext(spot.getStartingAt());
        Optional<E> optTargetPrev = this.findPrevious(spot.getStartingAt(), spot.getWatchTarget());

        boolean isPrevCombinable = optPrev.map(item -> this.mayMerge(item, spot)).orElse(false);
        boolean isNextCombinable = optNext.map(item -> this.mayMerge(spot, item)).orElse(false);

        if (isPrevCombinable && isNextCombinable) { // Dual way merge
            E prev = optPrev.get();
            E next = optNext.get();

            long newCount = prev.getEpisodeCount() + spot.getEpisodeCount() + next.getEpisodeCount();

            E updated = this.getManager().update(prev, item -> item.setEpisodeCount(newCount));

            // Copy to keep internal state updated.
            prev.setEpisodeCount(newCount);

            this.getManager().delete(next);
            this.state.remove(next); // This allows not destroying current instance.
            return updated;
        }

        if (isPrevCombinable) {

            E    prev     = optPrev.get();
            long newCount = prev.getEpisodeCount() + spot.getEpisodeCount();

            E updated = this.getManager().update(prev, item -> item.setEpisodeCount(newCount));

            // Copy to keep internal state updated.
            prev.setEpisodeCount(newCount);
            return updated;
        }

        if (isNextCombinable) {

            E    next     = optNext.get();
            long newCount = next.getEpisodeCount() + spot.getEpisodeCount();
            long firstEpisode = optTargetPrev
                    .map(item -> item.getFirstEpisode() + item.getEpisodeCount())
                    .orElseGet(() -> spot.getWatchTarget().getEpisodeWatched() + 1);

            E updated = this.getManager().update(next, item -> {
                item.setFirstEpisode(firstEpisode);
                item.setEpisodeCount(newCount);
                item.setStartingAt(spot.getStartingAt());
            });

            // Copy to keep internal state updated.
            next.setFirstEpisode(firstEpisode);
            next.setEpisodeCount(newCount);
            next.setStartingAt(spot.getStartingAt());

            return updated;
        }


        Plannifiable<T> plannifiable;
        if (optTargetPrev.isPresent()) {
            E prev = optTargetPrev.get();
            plannifiable = new BookedPlannifiable<>(spot, prev.getFirstEpisode() + prev.getEpisodeCount());
        } else {
            plannifiable = new BookedPlannifiable<>(spot);
        }

        E entity = this.getManager().create(plannifiable);
        this.state.add(entity);
        return entity;
    }

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
    @Override
    public List<E> delay(ZonedDateTime from, Duration interval, Duration delay) {

        ZonedDateTime to = from.plus(interval);

        List<E> events = this.getState()
                             .stream()
                             .filter(item -> DateTimeUtils.isAfterOrEquals(item.getStartingAt(), from))
                             .filter(item -> DateTimeUtils.isBeforeOrEquals(item.getEndingAt(), to))
                             .toList();

        // Creating a temporary state excluding events to delay to check for overlaps
        List<E> temporaryState = this.getState().stream().filter(item -> !events.contains(item)).toList();

        // Check if nothing overlaps the temporary state with the delay
        if (events.stream()
                  .map(item -> (Plannifiable<T>) new BookedPlannifiable<>(item))
                  .peek(item -> item.delayBy(delay))
                  .anyMatch(item -> temporaryState.stream().anyMatch(state -> this.isOverlapping(item, state)))) {

            throw new DelayOverlapException("One of the event cannot be delayed without conflict.");
        }

        // Apply the modification for real now
        List<E> updated = this.getManager().update(events, item -> item.delayBy(delay));
        // And update the internal state to keep track
        events.forEach(item -> item.delayBy(delay));

        return updated;
    }

    /**
     * Calibrate all {@link E} in this {@link Scheduler}. Calibration will reprocess every episode count and remove
     * superfluous events. This won't merge existing event.
     *
     * @return A {@link CalibrationResult} containing update/delete count.
     */
    @Override
    public CalibrationResult calibrate() {

        int updateCount = 0;
        int deleteCount = 0;

        // Store the max possible episode for each target
        Map<T, Long> targetMaxEpisode = this.getState()
                                            .stream()
                                            .map(ScheduleSpotData::getWatchTarget)
                                            .distinct()
                                            .collect(new MapCollector<>(WatchTarget::getEpisodeCount));

        // Store the progress for each target
        Map<T, Long> targetProgression = this.getState()
                                             .stream()
                                             .map(ScheduleSpotData::getWatchTarget)
                                             .distinct()
                                             .collect(new MapCollector<>(WatchTarget::getEpisodeWatched));

        List<E> sorted = this.getState()
                             .stream()
                             .sorted(Comparator.comparing(ScheduleSpotData::getStartingAt))
                             .toList();

        for (E event : sorted) {

            long maxEpisode  = targetMaxEpisode.get(event.getWatchTarget());
            long progression = targetProgression.get(event.getWatchTarget());

            boolean correctFirstEpisode = event.getFirstEpisode() == progression + 1;
            boolean correctEpisodeCount = (event.getFirstEpisode() + event.getEpisodeCount()) - 1 <= maxEpisode;

            long fixedFirstEpisode = progression + 1;
            long fixedEpisodeCount = Math.min(maxEpisode - progression, event.getEpisodeCount());

            // Don't keep overflowing events
            if (fixedFirstEpisode > maxEpisode) {
                this.getManager().delete(event);
                this.state.remove(event);
                deleteCount++;
                continue;
            }

            // If we require at least one thing to be updated, start the update
            if (!correctEpisodeCount || !correctFirstEpisode) {

                this.getManager().update(event, item -> {

                    item.setFirstEpisode(fixedFirstEpisode);
                    item.setEpisodeCount(fixedEpisodeCount);
                });

                event.setFirstEpisode(fixedFirstEpisode);
                event.setEpisodeCount(fixedEpisodeCount);

                updateCount++;
            }

            // Keep track of our movement throughout the schedule
            targetProgression.put(event.getWatchTarget(), fixedFirstEpisode + fixedEpisodeCount - 1);
        }

        return new CalibrationResult(updateCount, deleteCount);
    }

    /**
     * Check if the two provided {@link ScheduleSpotData} can be merged. This is where the rule of merging should be
     * decided (timing, content, etc...)
     *
     * @param element
     *         The first {@link ScheduleSpotData}
     * @param plannifiable
     *         The second {@link ScheduleSpotData}
     *
     * @return True if both event can be merged, false otherwise.
     */
    private boolean mayMerge(ScheduleSpotData<T> element, ScheduleSpotData<T> plannifiable) {

        long breakTime  = Duration.between(element.getEndingAt(), plannifiable.getStartingAt()).toSeconds();
        long magnetTime = MERGE_MAGNET_LIMIT.toSeconds();

        boolean isWithinMagnetTime = breakTime <= magnetTime;
        boolean isSameGroup        = Objects.equals(element.getWatchTarget(), plannifiable.getWatchTarget());

        return isWithinMagnetTime && isSameGroup;
    }

}
