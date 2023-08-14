package me.anisekai.toshiko.enums;

import java.time.ZonedDateTime;
import java.util.function.Function;

public enum BroadcastFrequency {

    ONCE(null, "Une fois"),
    DAILY(date -> date.plusDays(1), "Quotidien"),
    WEEKLY(date -> date.plusDays(7), "Hebdomadaire");


    private final Function<ZonedDateTime, ZonedDateTime> dateModifier;
    private final String                                 displayName;

    BroadcastFrequency(Function<ZonedDateTime, ZonedDateTime> dateModifier, String displayName) {

        this.dateModifier = dateModifier;
        this.displayName  = displayName;
    }

    public boolean hasDateModifier() {

        return this.dateModifier != null;
    }

    public Function<ZonedDateTime, ZonedDateTime> getDateModifier() {

        return this.dateModifier;
    }

    public String getDisplayName() {

        return this.displayName;
    }

    public static BroadcastFrequency from(String name) {

        if (name == null) return ONCE; // Default value;
        return BroadcastFrequency.valueOf(name.toUpperCase());
    }
}
