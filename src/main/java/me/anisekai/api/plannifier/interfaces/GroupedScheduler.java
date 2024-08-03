package me.anisekai.api.plannifier.interfaces;

import java.util.Optional;

public interface GroupedScheduler<W, I extends WatchParty<W>, E extends I> extends Scheduler<I, E> {

    /**
     * Find the previous scheduled {@link E} in this {@link Scheduler} relative to the provided {@link WatchParty}.
     * <p>
     * Only the nearest {@link E} scheduled before the provided {@link WatchParty} from the same group. The returned
     * {@link WatchParty} <i>can</i> be direct, but it's not always the case.
     *
     * @param watchParty
     *         The {@link WatchParty} to which the other scheduled event will be tested against.
     *
     * @return  A {@link WatchParty}, if any has been found.
     */
    Optional<E> findPreviousOf(I watchParty);

    /**
     * Find the next scheduled {@link E} in this {@link Scheduler} relative to the provided {@link WatchParty}.
     * <p>
     * Only the nearest {@link E} scheduled after the provided {@link WatchParty} from the same group. The returned
     * {@link WatchParty} <i>can</i> be direct, but it's not always the case.
     *
     * @param watchParty
     *         The {@link WatchParty} to which the other scheduled event will be tested against.
     *
     * @return  A {@link WatchParty}, if any has been found.
     */
    Optional<E> findNextOf(I watchParty);

    /**
     * Find the direct previous scheduled {@link E} in this {@link Scheduler} relative to the provided
     * {@link WatchParty}.
     * <p>
     * A {@link WatchParty} is considered <i>direct</i> of another when there is no other {@link WatchParty} scheduled
     * between itself and the other one.
     * <p>
     * This is particularly useful if you plan to merge two {@link WatchParty} together if they are close enough.
     *
     * @param watchParty
     *         The {@link WatchParty} to which the other scheduled event will be tested against.
     *
     * @return A {@link WatchParty}, if any has been found.
     */
    default Optional<E> findDirectPreviousOf(I watchParty) {

        return this.findPrevious(watchParty)
                   .filter(party -> party.getWatchTarget().equals(watchParty.getWatchTarget()));
    }

    /**
     * Find the direct next scheduled {@link E} in this {@link Scheduler} relative to the provided {@link WatchParty}.
     * <p>
     * A {@link WatchParty} is considered <i>direct</i> of another when there is no other {@link WatchParty} scheduled
     * between itself and the other one.
     * <p>
     * This is particularly useful if you plan to merge two {@link WatchParty} together if they are close enough.
     *
     * @param watchParty
     *         The {@link WatchParty} to which the other scheduled event will be tested against.
     *
     * @return A {@link WatchParty}, if any has been found.
     */
    default Optional<E> findDirectNextOf(I watchParty) {

        return this.findNext(watchParty)
                   .filter(party -> party.getWatchTarget().equals(watchParty.getWatchTarget()));
    }

}
