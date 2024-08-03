package me.anisekai.api.plannifier.interfaces;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Interface representing an object that can be plannified.
 */
public interface Plannifiable {

    static Plannifiable of(ZonedDateTime time, Duration duration) {

        return new Plannifiable() {
            @Override
            public ZonedDateTime getStartingAt() {

                return time;
            }

            @Override
            public void setStartingAt(ZonedDateTime time) {

                throw new UnsupportedOperationException("This value is read-only.");
            }

            @Override
            public Duration getDuration() {

                return duration;
            }

            @Override
            public void setDuration(Duration duration) {

                throw new UnsupportedOperationException("This value is read-only.");
            }
        };
    }

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link Plannifiable} will take place.
     *
     * @return A {@link ZonedDateTime}
     */
    ZonedDateTime getStartingAt();

    /**
     * Define the {@link ZonedDateTime} at which this {@link Plannifiable} will take place.
     *
     * @param time
     *         A {@link ZonedDateTime}
     */
    void setStartingAt(ZonedDateTime time);

    /**
     * Retrieve the total {@link Duration} of this {@link Plannifiable}.
     *
     * @return A {@link Duration}
     */
    Duration getDuration();

    /**
     * Set the total {@link Duration} of this {@link Plannifiable}.
     *
     * @param duration
     *         A {@link Duration}
     */
    void setDuration(Duration duration);

    /**
     * Delay the starting date of this {@link Plannifiable} by the provided {@link Duration}.
     *
     * @param modBy
     *         The {@link Duration} to delay
     */
    default void delayBy(Duration modBy) {

        this.setStartingAt(this.getStartingAt().plus(modBy));
    }

    /**
     * Extends the duration of this {@link Plannifiable} by the provided {@link Duration}.
     *
     * @param modBy
     *         The {@link Duration} to delay
     */
    default void extendsBy(Duration modBy) {

        this.setDuration(this.getDuration().plus(modBy));
    }

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link Plannifiable} will end. This is merely a shorthand for
     * {@link #getStartingAt()} + {@link #getDuration()}.
     *
     * @return A {@link ZonedDateTime}
     */
    default ZonedDateTime getEndingAt() {

        return this.getStartingAt().plus(this.getDuration());
    }

}
