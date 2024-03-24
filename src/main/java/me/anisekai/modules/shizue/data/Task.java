package me.anisekai.modules.shizue.data;

import me.anisekai.modules.shizue.interfaces.ThrowingRunnable;

public interface Task extends ThrowingRunnable {

    /**
     * Retrieve this {@link Task} name.
     *
     * @return The name
     */
    String getName();

    /**
     * Called when this {@link Task} {@link #run()} method has completed successfully.
     */
    default void onFinished() {}

    /**
     * Called when this {@link Task} {@link #run()} has thrown an exception.
     *
     * @param e
     *         The {@link Exception} that has been thrown.
     */
    default void onException(Exception e) {}

}
