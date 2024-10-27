package me.anisekai.globals.tasking.interfaces;

import me.anisekai.globals.tasking.Task;

public interface TaskFactory<T extends TaskExecutor> {

    Class<T> getTaskClass();

    /**
     * Get this {@link TaskFactory} name, which will be used to associate it with a {@link Task}.
     *
     * @return The {@link TaskFactory} name.
     */
    String getName();

    /**
     * Create an instance of {@link TaskExecutor}. This is useful if your task has some bean dependencies.
     *
     * @return A new {@link TaskExecutor} instance.
     */
    T create();

    /**
     * Check if this {@link TaskFactory} allows multiple {@link Task} with the same {@link Task#getName()}.
     *
     * @return True if duplicates are allowed, false otherwise.
     */
    default boolean allowDuplicated() {

        return false;
    }

}
