package me.anisekai.toshiko.enums;

public enum AnimeUpdateType {

    ADDED(true),
    RELEASING(true),
    RELEASED(false),
    UPDATE(false);

    private final boolean notify;

    AnimeUpdateType(boolean notify) {

        this.notify = notify;
    }

    public boolean shouldNotify() {

        return this.notify;
    }
}
