package me.anisekai.toshiko.enums;

import net.dv8tion.jda.api.entities.ScheduledEvent;

import java.awt.*;

public enum ScheduledEventState {

    SCHEDULED(ScheduledEvent.Status.SCHEDULED, Color.CYAN),
    OPENED(ScheduledEvent.Status.ACTIVE, Color.WHITE),
    CANCELLED(ScheduledEvent.Status.CANCELED, Color.RED),
    FINISHED(ScheduledEvent.Status.COMPLETED, Color.GREEN);

    private final ScheduledEvent.Status discordStatus;
    private final Color                 color;

    ScheduledEventState(ScheduledEvent.Status discordStatus, Color color) {

        this.discordStatus = discordStatus;
        this.color         = color;
    }

    public ScheduledEvent.Status getDiscordStatus() {

        return this.discordStatus;
    }

    public Color getColor() {

        return this.color;
    }
}
