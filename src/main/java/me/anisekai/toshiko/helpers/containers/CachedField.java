package me.anisekai.toshiko.helpers.containers;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class CachedField<T> {

    private T             value;
    private LocalDateTime lastUpdated;
    private int           timeout;

    public void set(T value) {

        this.value       = value;
        this.timeout     = 1;
        this.lastUpdated = LocalDateTime.now();
    }

    public void timeout(int timeout) {

        this.timeout = timeout;
    }

    public boolean isCacheValid() {

        Duration duration = Duration.between(this.lastUpdated, LocalDateTime.now());
        return this.value != null && duration.getSeconds() < this.timeout;
    }

    public T get() {

        if (this.isCacheValid()) {
            return this.value;
        }
        this.value = null; // Memory cleaning
        return null;
    }
}
