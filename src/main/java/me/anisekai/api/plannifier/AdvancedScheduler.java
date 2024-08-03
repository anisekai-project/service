package me.anisekai.api.plannifier;

import me.anisekai.api.plannifier.exceptions.NotSchedulableException;
import me.anisekai.api.plannifier.exceptions.SchedulingCreationException;
import me.anisekai.api.plannifier.interfaces.*;
import me.anisekai.globals.exceptions.SilentDiscordException;
import me.anisekai.globals.utils.DateTimeUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class AdvancedScheduler<W, I extends WatchParty<W>, E extends I> extends SimpleScheduler<I, E> implements GroupedScheduler<W, I, E> {

    /**
     * If two {@link WatchParty} are of the following duration apart, they will be merged once scheduled. The duration
     * is done by comparing one {@link WatchParty#getStartingAt()} with the endpoint of another {@link WatchParty},
     * being {@link WatchParty#getStartingAt()} + {@link WatchParty#getDuration()}.
     */
    private static final Duration MERGE_MAGNET_LIMIT = Duration.ofMinutes(10);

    /**
     * Create a new instance of {@link AdvancedScheduler}.
     *
     * @param manager
     *         The {@link PlannificationManager} that will be used for this instance.
     */
    public AdvancedScheduler(PlannificationManager<I, E> manager) {

        super(manager);
    }

    /**
     * Create a new instance of {@link AdvancedScheduler}.
     *
     * @param initialItems
     *         The initial collection of items to load in the scheduler.
     * @param manager
     *         The {@link PlannificationManager} that will be used for this instance.
     */
    public AdvancedScheduler(Collection<E> initialItems, PlannificationManager<I, E> manager) {

        super(initialItems, manager);
    }

    /**
     * Find the previous scheduled {@link E} in this {@link Scheduler} relative to the provided {@link WatchParty}.
     * <p>
     * Only the nearest {@link E} scheduled before the provided {@link WatchParty} from the same group. The returned
     * {@link WatchParty} <i>can</i> be direct, but it's not always the case.
     *
     * @param watchParty
     *         The {@link WatchParty} to which the other scheduled event will be tested against.
     *
     * @return A {@link WatchParty}, if any has been found.
     */
    @Override
    public Optional<E> findPreviousOf(I watchParty) {

        return this.getState().stream()
                   .filter(party -> party.getWatchTarget().equals(watchParty.getWatchTarget()))
                   .filter(party -> DateTimeUtils.isBeforeOrEquals(watchParty.getStartingAt(), party.getStartingAt()))
                   .max(Comparator.comparing(WatchParty::getStartingAt));
    }

    /**
     * Find the next scheduled {@link E} in this {@link Scheduler} relative to the provided {@link WatchParty}.
     * <p>
     * Only the nearest {@link E} scheduled after the provided {@link WatchParty} from the same group. The returned
     * {@link WatchParty} <i>can</i> be direct, but it's not always the case.
     *
     * @param watchParty
     *         The {@link WatchParty} to which the other scheduled event will be tested against.
     *
     * @return A {@link WatchParty}, if any has been found.
     */
    @Override
    public Optional<E> findNextOf(I watchParty) {

        return this.getState().stream()
                   .filter(party -> party.getWatchTarget().equals(watchParty.getWatchTarget()))
                   .filter(party -> DateTimeUtils.isAfterOrEquals(watchParty.getStartingAt(), party.getStartingAt()))
                   .max(Comparator.comparing(WatchParty::getStartingAt));
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
                // Ok, should we merge now ?
                Optional<E> optionalPrevious = this.findDirectPreviousOf(plannifiable);
                Optional<E> optionalNext     = this.findDirectNextOf(plannifiable);

                boolean isPreviousCombinable = optionalPrevious.isPresent() && this.mayMerge(
                        optionalPrevious.get(),
                        plannifiable
                );
                boolean isNextCombinable = optionalNext.isPresent() && this.mayMerge(
                        plannifiable,
                        optionalNext.get()
                );

                if (isPreviousCombinable && isNextCombinable) { // Dual Way Merge

                    E previous = optionalPrevious.get();
                    E next     = optionalNext.get();

                    previous.setEpisodeCount(previous.getEpisodeCount() + plannifiable.getEpisodeCount() + next.getEpisodeCount());

                    this.getManager().requestDelete(next);
                    return this.getManager().requestUpdate(previous);

                } else if (isPreviousCombinable) { // Backward Merge
                    E previous = optionalPrevious.get();
                    previous.setEpisodeCount(previous.getEpisodeCount() + plannifiable.getEpisodeCount());
                    return this.getManager().requestUpdate(previous);

                } else if (isNextCombinable) { // Forward merge
                    E next = optionalNext.get();
                    next.setEpisodeCount(next.getEpisodeCount() + plannifiable.getEpisodeCount());
                    next.setStartingAt(plannifiable.getStartingAt());
                    return this.getManager().requestUpdate(next);
                }

                E result = this.getManager().requestCreate(plannifiable);
                this.getState().add(result);
                return result;
            } catch (Exception e) {
                throw new SchedulingCreationException(e);
            }
        }

        throw new NotSchedulableException();
    }

    /**
     * Check if the two provided {@link Plannifiable} can be merged. This is where the rule of merging should be decided
     * (timing, content, etc...)
     *
     * @param element
     *         The first {@link Plannifiable}
     * @param plannifiable
     *         The second {@link Plannifiable}
     *
     * @return True if both event can be merged, false otherwise.
     */
    protected boolean mayMerge(I element, I plannifiable) {

        long breakTime  = Duration.between(element.getEndingAt(), plannifiable.getStartingAt()).toSeconds();
        long magnetTime = MERGE_MAGNET_LIMIT.toSeconds();

        boolean isWithinMagnetTime = breakTime <= magnetTime;
        boolean isSameGroup = Objects.equals(element.getWatchTarget(), plannifiable.getWatchTarget());

        return isWithinMagnetTime && isSameGroup;
    }

}
