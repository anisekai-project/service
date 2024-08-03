package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.Plannifiable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

public class T_Plannifiable implements Plannifiable {

    private final Long          id;
    private       ZonedDateTime startingAt;
    private       Duration      duration;

    public T_Plannifiable(Long id, ZonedDateTime startingAt, Duration duration) {

        this.id         = id;
        this.startingAt = startingAt;
        this.duration   = duration;
    }

    @Override
    public ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    @Override
    public void setStartingAt(ZonedDateTime startingAt) {

        this.startingAt = startingAt;
    }

    @Override
    public Duration getDuration() {

        return this.duration;
    }

    @Override
    public void setDuration(Duration duration) {

        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        T_Plannifiable item = (T_Plannifiable) o;
        return Objects.equals(this.id, item.id);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.id);
    }

}
