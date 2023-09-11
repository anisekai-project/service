package me.anisekai.toshiko.enums;

import java.util.Arrays;
import java.util.List;

// TODO: Transform into entity
public enum AnimeStatus {

    WATCHED("✅", "Visionné"),
    WATCHING("👀", "En cours de visionnage", false),
    SIMULCAST("\uD83D\uDD58", "Visionnage en simulcast", false),
    SIMULCAST_AVAILABLE("✨", "En cours de diffusion", true),
    DOWNLOADED("\uD83D\uDCD7", "Téléchargé", true),
    DOWNLOADING("\uD83D\uDCD8", "En cours de téléchargement", false),
    NOT_DOWNLOADED("\uD83D\uDCD5", "Non téléchargé", false),
    NO_SOURCE("\uD83D\uDCD9", "Pas de source trouvée", false),
    UNAVAILABLE("\uD83D\uDD16", "Pas encore sorti", false),
    CANCELLED("\uD83D\uDED1", "Abandonné");

    private final String  icon;
    private final String  label;
    private final boolean watchable;
    private final boolean displayList;

    AnimeStatus(String icon, String label) {

        this.icon        = icon;
        this.label       = label;
        this.watchable   = false;
        this.displayList = false;
    }

    AnimeStatus(String icon, String label, boolean watchable) {

        this.icon        = icon;
        this.label       = label;
        this.watchable   = watchable;
        this.displayList = true;
    }

    public static AnimeStatus from(String value) {

        String upperValue = value.toUpperCase();
        try {
            return AnimeStatus.valueOf(upperValue);
        } catch (IllegalArgumentException e) {
            return AnimeStatus.UNAVAILABLE;
        }
    }

    public static List<AnimeStatus> getDisplayable() {

        return Arrays.stream(AnimeStatus.values()).filter(AnimeStatus::shouldDisplayList).sorted().toList();
    }

    public static List<AnimeStatus> getSchedulable() {
        return Arrays.asList(WATCHING, SIMULCAST);
    }

    public boolean shouldDisplayList() {

        return this.displayList;
    }

    public boolean isWatchable() {

        return this.watchable;
    }

    public String getDisplay() {

        return String.format("%s %s", this.icon, this.label);
    }

    public String getIcon() {

        return this.icon;
    }
}
