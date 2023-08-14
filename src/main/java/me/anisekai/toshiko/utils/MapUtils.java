package me.anisekai.toshiko.utils;

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
     * Create a {@link Map} from a {@link Collection} by generating key using the provided {@link Function}.
     * <p>
     * This is particularly useful when you have a collection of objects with a common property, and you want them to be
     * grouped using that property.
     *
     * @param collection
     *         The {@link Collection} of object from which the {@link Map} will be generated.
     * @param classifier
     *         The {@link Function} to use to retrieve the key for a given object.
     * @param <K>
     *         The type of the key that will be used.
     * @param <V>
     *         The type of the object within the collection provided.
     *
     * @return A {@link Map} associating each object to a given property.
     */
    public static <K, V> Map<K, List<V>> groupBy(@NotNull Collection<V> collection, Function<? super V, ? extends K> classifier) {

        return collection.stream().collect(Collectors.groupingBy(classifier, HashMap::new, Collectors.toList()));
    }

    public static <K, V> Map<K, V> mapBy(@NotNull Collection<V> collection, Function<? super V, ? extends K> classifier) {

        return collection.stream().collect(Collectors.toMap(classifier, item -> item));
    }

}
