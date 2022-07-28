package me.anisekai.toshiko.tasks.entity;

public class TaskEntry {

    private final String   name;
    private final Runnable runnable;

    public TaskEntry(String name, Runnable runnable) {

        this.name     = name;
        this.runnable = runnable;
    }

    public String getName() {

        return this.name;
    }

    public Runnable getRunnable() {

        return this.runnable;
    }
}
