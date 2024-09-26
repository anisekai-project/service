package me.anisekai.api.plannifier.interfaces;

import java.util.List;
import java.util.function.Consumer;

public interface SchedulerManager<T extends WatchTarget, I extends Plannifiable<T>, E extends I> {

    E create(Plannifiable<T> plannifiable);

    E update(E entity, Consumer<I> updateHook);

    List<E> update(List<E> entities, Consumer<I> updateHook);

    boolean delete(E entity);

}
