package me.anisekai.api.persistence;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public final class EntityUtils {

    private EntityUtils() {}

    public static <E extends IEntity<PK>, PK extends Serializable> boolean equals(E entity, E other) {

        if (entity.isNew() || other.isNew()) {
            return false;
        }
        return Objects.equals(entity.getId(), other.getId());
    }

    public static <E extends IEntity<PK>, PK extends Serializable, V> boolean equals(E entity, E other, Function<E, V> externalCheck) {

        if (entity.isNew() || other.isNew()) {
            return Objects.equals(externalCheck.apply(entity), externalCheck.apply(other));
        }
        return Objects.equals(entity.getId(), other.getId());
    }

    public static <E extends IEntity<PK>, PK extends Serializable & Comparable<PK>> int compare(E entity, E other) {

        if (entity.isNew() && other.isNew()) {
            return 0;
        }
        if (entity.isNew() && !other.isNew()) {
            return 1;
        }
        if (!entity.isNew() && other.isNew()) {
            return -1;
        }
        return entity.getId().compareTo(other.getId());
    }

    @SafeVarargs
    public static <E extends IEntity<PK>, PK extends Serializable & Comparable<PK>> int compare(E entity, E other, Comparator<E>... compares) {

        for (Comparator<E> compare : compares) {
            int comp = compare.compare(entity, other);
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }

}
