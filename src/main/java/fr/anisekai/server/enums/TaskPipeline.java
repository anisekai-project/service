package fr.anisekai.server.enums;

import fr.anisekai.server.tasking.TaskFactory;

/**
 * Enum listing all available pipeline.
 * <p>
 * A pipeline allow to separate {@link TaskFactory} into multiple categories so they can work in parallel without
 * hindering the execution of others.
 */
public enum TaskPipeline {

    /**
     * Pipeline reserved for long-running task.
     */
    HEAVY,

    /**
     * Pipeline reserved for regular task
     */
    SOFT,

    /**
     * Pipeline reserved for short-running task
     */
    MESSAGING

}
