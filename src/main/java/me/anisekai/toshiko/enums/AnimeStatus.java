package me.anisekai.toshiko.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AnimeStatus {

    WATCHED("✅", "Visionné", PublicationState.FINISHED, false, false),
    WATCHING("👀", "En cours de visionnage", PublicationState.FINISHED, false),
    SIMULCAST("\uD83D\uDD58", "Visionnage en simulcast", PublicationState.AIRING, false),
    SIMULCAST_AVAILABLE("✨", "En cours de diffusion", PublicationState.AIRING, true),
    DOWNLOADED("\uD83D\uDCD7", "Téléchargé", PublicationState.FINISHED, true),
    DOWNLOADING("\uD83D\uDCD8", "En cours de téléchargement", PublicationState.FINISHED, false),
    NOT_DOWNLOADED("\uD83D\uDCD5", "Non téléchargé", PublicationState.FINISHED, false),
    NO_SOURCE("\uD83D\uDCD9", "Pas de source trouvée", PublicationState.FINISHED, false),
    UNAVAILABLE("\uD83D\uDD16", "Pas encore sorti", PublicationState.UNAVAILABLE, false),
    CANCELLED("\uD83D\uDED1", "Abandonné", PublicationState.FINISHED, false, false);

    private final String           icon;
    private final String           label;
    private final PublicationState state;
    private final boolean          watchable;
    private final boolean          displayList;

    AnimeStatus(String icon, String label, PublicationState state, boolean watchable, boolean displayList) {

        this.icon        = icon;
        this.label       = label;
        this.state       = state;
        this.watchable   = watchable;
        this.displayList = displayList;
    }

    AnimeStatus(String icon, String label, PublicationState state, boolean watchable) {

        this.icon        = icon;
        this.label       = label;
        this.state       = state;
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

    public static List<AnimeStatus> getWatchable() {

        return Arrays.stream(AnimeStatus.values()).filter(AnimeStatus::isWatchable).sorted().toList();
    }

    public String getIcon() {

        return this.icon;
    }

    public String getLabel() {

        return this.label;
    }

    public PublicationState getState() {

        return this.state;
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

    public Map<String, Object> asMap() {

        return new HashMap<>() {{
            this.put("name", AnimeStatus.this.name());
            this.put("icon", AnimeStatus.this.icon);
            this.put("label", AnimeStatus.this.label);
            this.put("displayable", AnimeStatus.this.shouldDisplayList());
        }};
    }
}
