package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.events.AnimeNightUpdateEvent;

import java.time.OffsetDateTime;

public final class AnimeNights {

    private AnimeNights() {}

    public static String createDescription(Anime anime, long amount) {

        return createDescription(amount, anime.getWatched());
    }

    public static String createDescription(AnimeNightUpdateEvent event) {

        return createDescription(event.getAnimeNight().getAmount(), event.getStartingFrom());
    }

    public static boolean isOverlapping(AnimeNight animeNight, OffsetDateTime start, OffsetDateTime end) {

        boolean isSameStart   = start.isEqual(animeNight.getStartTime());
        boolean isSameEnd     = end.isEqual(animeNight.getEndTime());
        boolean isStartDuring = start.isAfter(animeNight.getStartTime()) && start.isBefore(animeNight.getEndTime());
        boolean isEndDuring   = end.isAfter(animeNight.getStartTime()) && end.isBefore(animeNight.getEndTime());
        boolean isOverride    = start.isBefore(animeNight.getStartTime()) && end.isAfter(animeNight.getEndTime());

        return isSameStart || isSameEnd || isStartDuring || isEndDuring || isOverride;
    }

    public static String createDescription(long amount, long lastWatched) {

        String descriptionSingleFormat   = "**Épisode** %s";
        String descriptionMultipleFormat = "**Épisodes** %s %s %s";

        if (amount == 1) {
            return descriptionSingleFormat.formatted(lastWatched + amount);
        } else {
            return descriptionMultipleFormat.formatted(
                    lastWatched + 1,
                    amount == 2 ? "et" : "à",
                    lastWatched + amount
            );
        }
    }

}
