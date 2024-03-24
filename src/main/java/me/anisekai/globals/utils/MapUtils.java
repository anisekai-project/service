package me.anisekai.globals.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class to handle {@link Map} related methods.
 */
public final class MapUtils {

    private MapUtils() {}

    /**
     * Creates a {@link Map} from a {@link Collection} by grouping elements based on a provided key extractor function.
     * <p>
     * This method is particularly useful when you have a collection of objects with common properties, and you want to
     * group them based on those properties.
     *
     * @param collection
     *         The {@link Collection} of objects from which the {@link Map} will be generated
     * @param classifier
     *         The {@link Function} to extract the key for each object
     * @param <K>
     *         The type of the keys in the resulting map
     * @param <V>
     *         The type of the objects in the collection
     *
     * @return a {@link Map} associating each object's property to a list of objects with that property
     */
    public static <K, V> Map<K, List<V>> groupBy(@NotNull Collection<V> collection, Function<? super V, ? extends K> classifier) {

        return collection.stream().collect(Collectors.groupingBy(classifier, HashMap::new, Collectors.toList()));
    }

    /**
     * Creates a {@link Map} from a {@link Collection} by mapping each element to a key-value pair using a provided key
     * extractor function.
     * <p>
     * This method is particularly useful when you have a collection of objects with different property values, and you
     * want to map them directly to those properties.
     *
     * @param collection
     *         The {@link Collection} of objects from which the {@link Map} will be generated
     * @param classifier
     *         The {@link Function} to extract the key for each object
     * @param <K>
     *         The type of the keys in the resulting map
     * @param <V>
     *         The type of the objects in the collection
     *
     * @return a {@link Map} associating each object's property directly to the object
     */
    public static <K, V> Map<K, V> mapBy(@NotNull Collection<V> collection, Function<? super V, ? extends K> classifier) {

        return collection.stream().collect(Collectors.toMap(classifier, item -> item));
    }

}
