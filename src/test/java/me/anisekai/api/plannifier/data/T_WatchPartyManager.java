package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.PlannificationManager;
import me.anisekai.api.plannifier.interfaces.Scheduler;
import me.anisekai.api.plannifier.interfaces.WatchParty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;

public class T_WatchPartyManager implements PlannificationManager<WatchParty<Long>, T_WatchParty> {

    public static T_WatchParty of(Long id, ZonedDateTime startingAt, Long watchTarget, Long episodeCount) {
        return new T_WatchParty(id, startingAt, watchTarget, episodeCount);
    }

    private long counter = 0;

    public long requestCreateCount = 0;
    public long requestUpdateCount = 0;
    public long requestDeleteCount = 0;

    private long id() {

        this.counter++;
        return this.counter;
    }

    /**
     * Called when a {@link Scheduler} request that a {@link Plannifiable} should be persisted.
     *
     * @param plannifiable
     *         The {@link Plannifiable} entity to create.
     *
     * @return Should return the saved entity in case of success, or otherwise will throw.
     */
    @Override
    public @NotNull T_WatchParty requestCreate(@NotNull WatchParty<Long> plannifiable) {

        this.requestCreateCount++;
        return new T_WatchParty(this.id(), plannifiable.getStartingAt(), plannifiable.getWatchTarget(), plannifiable.getEpisodeCount());
    }

    /**
     * Called when a {@link Scheduler} request that a {@link Plannifiable} should be updated.
     *
     * @param plannifiable
     *         The {@link Plannifiable} entity to update.
     *
     * @return Should return the saved entity in case of success, or otherwise {@code null}.
     */
    @Override
    public @Nullable T_WatchParty requestUpdate(@NotNull T_WatchParty plannifiable) {

        this.requestUpdateCount++;
        return plannifiable;
    }

    /**
     * Called when a {@link Scheduler} request to remove a {@link Plannifiable}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} entity to remove.
     *
     * @return If true, the deletion will be considered as successful and cleared from {@link Scheduler}.
     */
    @Override
    public boolean requestDelete(@NotNull T_WatchParty plannifiable) {

        this.requestDeleteCount++;
        plannifiable.setTestTagDeleted(true);
        return true;
    }

}
