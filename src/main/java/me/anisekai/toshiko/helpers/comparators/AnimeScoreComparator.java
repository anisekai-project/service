package me.anisekai.toshiko.helpers.comparators;

import me.anisekai.toshiko.entities.Anime;

import java.util.Comparator;
import java.util.Map;

public class AnimeScoreComparator implements Comparator<Map.Entry<Anime, Double>> {

    @Override
    public int compare(Map.Entry<Anime, Double> entryA, Map.Entry<Anime, Double> entryB) {

        int compare = entryB.getValue().compareTo(entryA.getValue());
        return compare == 0 ? entryA.getKey().getId().compareTo(entryB.getKey().getId()) : compare;
    }
}
