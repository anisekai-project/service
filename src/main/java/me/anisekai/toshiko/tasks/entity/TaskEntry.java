package me.anisekai.toshiko.tasks.entity;

import java.util.function.Consumer;

public class TaskEntry {

    private final String              name;
    private final Runnable            runnable;
    private final Consumer<Exception> failed;

    public TaskEntry(String name, Runnable runnable) {

        this.name     = name;
        this.runnable = runnable;
        this.failed   = (e) -> {};
    }

    public TaskEntry(String name, Runnable runnable, Consumer<Exception> failed) {

        this.name     = name;
        this.runnable = runnable;
        this.failed   = failed;
    }

    public String getName() {

        return this.name;
    }

    public Runnable getRunnable() {

        return this.runnable;
    }

    public Consumer<Exception> getFailed() {

        return this.failed;
    }
}
