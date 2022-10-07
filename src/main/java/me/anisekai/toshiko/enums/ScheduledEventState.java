package me.anisekai.toshiko.enums;

import java.awt.*;

public enum ScheduledEventState {

    SCHEDULED(Color.CYAN),
    OPENED(Color.WHITE),
    CANCELLED(Color.RED),
    FINISHED(Color.GREEN);

    private final Color color;

    ScheduledEventState(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return this.color;
    }
}
