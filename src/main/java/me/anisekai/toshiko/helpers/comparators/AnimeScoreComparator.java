package me.anisekai.toshiko.helpers.comparators;

import me.anisekai.toshiko.entities.Anime;

import java.util.Comparator;
import java.util.Map;

public class AnimeScoreComparator implements Comparator<Map.Entry<Anime, Double>> {

    private final boolean reverse;

    public AnimeScoreComparator(boolean reverse) {

        this.reverse = reverse;
    }

    @Override
    public int compare(Map.Entry<Anime, Double> entryA, Map.Entry<Anime, Double> entryB) {

        int compare;
        if (this.reverse) {
            compare = entryB.getValue().compareTo(entryA.getValue());
        } else {
            compare = entryA.getValue().compareTo(entryB.getValue());
        }

        return compare == 0 ? entryA.getKey().getId().compareTo(entryB.getKey().getId()) : compare;
    }
}
