package me.anisekai.utils;

import me.anisekai.api.plannifier.interfaces.entities.Plannifiable;

public final class StringUtils {

    private StringUtils() {}

    public static String count(int count, String none, String one, String multiple) {

        if (count == 0) {
            return String.format(none, count);
        } else if (count == 1) {
            return String.format(none, one);
        } else {
            return String.format(multiple, count);
        }
    }

    public static String getPlannifiableDescription(Plannifiable<?> plannifiable) {

        return getPlannifiableDescription(plannifiable, true);
    }

    public static String getPlannifiableDescription(Plannifiable<?> plannifiable, boolean emphasize) {

        long amount = plannifiable.getEpisodeCount();

        if (amount == 1) {
            String prefix = emphasize ? "**Épisode**" : "épisode";
            return String.format("%s %02d", prefix, plannifiable.getFirstEpisode());
        } else if (amount == 2) {
            String prefix = emphasize ? "**Épisodes**" : "épisodes";
            return String.format(
                    "%s %02d et %02d",
                    prefix,
                    plannifiable.getFirstEpisode(),
                    plannifiable.getLastEpisode()
            );
        } else {
            String prefix = emphasize ? "**Épisodes**" : "épisodes";
            return String.format(
                    "%s %02d à %02d",
                    prefix,
                    plannifiable.getFirstEpisode(),
                    plannifiable.getLastEpisode()
            );
        }
    }

}
