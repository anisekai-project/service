package me.anisekai.globals.utils.collectors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MapCollector<T, K> implements Collector<T, Map<T, K>, Map<T, K>> {

    private final Function<T, K> valueMapper;

    public MapCollector(Function<T, K> valueMapper) {

        this.valueMapper = valueMapper;
    }

    /**
     * A function that creates and returns a new mutable result container.
     *
     * @return a function which returns a new, mutable result container
     */
    @Override
    public Supplier<Map<T, K>> supplier() {

        return HashMap::new;
    }

    /**
     * A function that folds a value into a mutable result container.
     *
     * @return a function which folds a value into a mutable result container
     */
    @Override
    public BiConsumer<Map<T, K>, T> accumulator() {

        return (map, item) -> map.put(item, this.valueMapper.apply(item));
    }

    /**
     * A function that accepts two partial results and merges them.  The combiner function may fold state from one argument
     * into the other and return that, or may return a new result container.
     *
     * @return a function which combines two partial results into a combined result
     */
    @Override
    public BinaryOperator<Map<T, K>> combiner() {

        return (left, right) -> {
            left.putAll(right);
            return left;
        };
    }

    /**
     * Perform the final transformation from the intermediate accumulation type {@code A} to the final result type
     * {@code R}.
     *
     * <p>If the characteristic {@code IDENTITY_FINISH} is
     * set, this function may be presumed to be an identity transform with an unchecked cast from {@code A} to {@code R}.
     *
     * @return a function which transforms the intermediate result to the final result
     */
    @Override
    public Function<Map<T, K>, Map<T, K>> finisher() {

        return item -> item;
    }

    /**
     * Returns a {@code Set} of {@code Collector.Characteristics} indicating the characteristics of this Collector.  This
     * set should be immutable.
     *
     * @return an immutable set of collector characteristics
     */
    @Override
    public Set<Characteristics> characteristics() {

        return Set.of(Characteristics.UNORDERED);
    }

}
