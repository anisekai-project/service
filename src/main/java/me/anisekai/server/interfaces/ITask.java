package me.anisekai.server.interfaces;

import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.server.enums.TaskState;

import java.time.ZonedDateTime;

public interface ITask extends IEntity<Long> {

    /**
     * Retrieve this {@link ITask}'s factory name.
     *
     * @return A name.
     */
    String getFactory();

    /**
     * Define this {@link ITask}'s factory name.
     *
     * @param factory
     *         A name.
     */
    void setFactory(String factory);

    /**
     * Retrieve this {@link ITask}'s name.
     *
     * @return A name.
     */
    String getName();

    /**
     * Define this {@link ITask}'s name.
     *
     * @param name
     *         A name.
     */
    void setName(String name);

    /**
     * Retrieve this {@link ITask}'s state.
     *
     * @return A {@link TaskState}.
     */
    TaskState getState();

    /**
     * Define this {@link ITask}'s state.
     *
     * @param state
     *         A {@link TaskState}.
     */
    void setState(TaskState state);

    /**
     * Retrieve this {@link ITask}'s priority. Higher values mean more importance in the priority list.
     *
     * @return A priority.
     */
    long getPriority();

    /**
     * Define this {@link ITask}'s priority. Higher values mean more importance in the priority list.
     *
     * @param priority
     *         A priority.
     */
    void setPriority(long priority);

    /**
     * Retrieve this {@link ITask}'s arguments, which are optional data the task need to be executed.
     *
     * @return A {@link BookshelfJson}.
     */
    BookshelfJson getArguments();

    /**
     * Define this {@link ITask}'s arguments, which are optional data the task need to be executed.
     *
     * @param arguments
     *         A {@link BookshelfJson}.
     */
    void setArguments(BookshelfJson arguments);

    /**
     * Retrieve the amount of time this {@link ITask}'s failed to execute.
     *
     * @return A failure count.
     */
    long getFailureCount();

    /**
     * Define the amount of time this {@link ITask}'s failed to execute.
     *
     * @param failureCount
     *         A failure count.
     */
    void setFailureCount(long failureCount);

    /**
     * Retrieve when this {@link ITask}'s started its execution.
     *
     * @return A {@link ZonedDateTime}.
     */
    ZonedDateTime getStartedAt();

    /**
     * Define when this {@link ITask}'s started its execution.
     *
     * @param startedAt
     *         A {@link ZonedDateTime}.
     */
    void setStartedAt(ZonedDateTime startedAt);

    /**
     * Retrieve when this {@link ITask}'s finished its execution.
     *
     * @return A {@link ZonedDateTime}.
     */
    ZonedDateTime getCompletedAt();

    /**
     * Define when this {@link ITask}'s finished its execution.
     *
     * @param completedAt
     *         A {@link ZonedDateTime}.
     */
    void setCompletedAt(ZonedDateTime completedAt);

}
