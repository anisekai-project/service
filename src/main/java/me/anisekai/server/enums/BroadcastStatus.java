package me.anisekai.server.enums;

import net.dv8tion.jda.api.entities.ScheduledEvent;

public enum BroadcastStatus {

    UNSCHEDULED(false),
    SCHEDULED(true),
    ACTIVE(true),
    COMPLETED(false),
    CANCELED(false);

    private final boolean cancelable;

    BroadcastStatus(boolean cancelable) {

        this.cancelable = cancelable;
    }

    public static BroadcastStatus ofDiscordStatus(ScheduledEvent.Status status) {

        return switch (status) {
            case SCHEDULED -> SCHEDULED;
            case ACTIVE -> ACTIVE;
            case COMPLETED -> COMPLETED;
            case CANCELED -> CANCELED;
            default -> UNSCHEDULED;
        };
    }

    public boolean isCancelable() {

        return this.cancelable;
    }
}
