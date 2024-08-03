package me.anisekai.api.plannifier.interfaces;

import me.anisekai.api.plannifier.SimpleScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface allowing communication between {@link SimpleScheduler} and any data management class.
 *
 * @param <E>
 *         Type of the {@link Plannifiable} entity.
 * @param <I>
 *         Type of the {@link Plannifiable} root interface.
 */
public interface PlannificationManager<I extends Plannifiable, E extends I> {

    /**
     * Called when a {@link Scheduler} request that a {@link Plannifiable} should be persisted.
     *
     * @param plannifiable
     *         The {@link Plannifiable} entity to create.
     *
     * @return Should return the saved entity in case of success, or otherwise will throw.
     *
     * @throws Exception
     *         Threw if the {@link Plannifiable} could not be saved.
     */
    @NotNull
    E requestCreate(@NotNull I plannifiable) throws Exception;

    /**
     * Called when a {@link Scheduler} request that a {@link Plannifiable} should be updated.
     *
     * @param plannifiable
     *         The {@link Plannifiable} entity to update.
     *
     * @return Should return the saved entity in case of success, or otherwise {@code null}.
     */
    @Nullable
    E requestUpdate(@NotNull E plannifiable);

    /**
     * Called when a {@link Scheduler} request to remove a {@link Plannifiable}.
     *
     * @param plannifiable
     *         The {@link Plannifiable} entity to remove.
     *
     * @return If true, the deletion will be considered as successful and cleared from {@link Scheduler}.
     */
    boolean requestDelete(@NotNull E plannifiable);

}
