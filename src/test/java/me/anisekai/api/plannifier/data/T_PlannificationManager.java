package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.PlannificationManager;
import me.anisekai.api.plannifier.interfaces.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class T_PlannificationManager implements PlannificationManager<Plannifiable, T_Plannifiable> {

    private long counter = 0;
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
     *
     */
    @Override
    public @NotNull T_Plannifiable requestCreate(@NotNull Plannifiable plannifiable) {

        return new T_Plannifiable(this.id(), plannifiable.getStartingAt(), plannifiable.getDuration());
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
    public @Nullable T_Plannifiable requestUpdate(@NotNull T_Plannifiable plannifiable) {

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
    public boolean requestDelete(@NotNull T_Plannifiable plannifiable) {

        return true;
    }

}
