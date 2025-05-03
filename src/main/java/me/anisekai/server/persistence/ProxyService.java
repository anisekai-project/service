package me.anisekai.server.persistence;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.api.persistence.interfaces.EventProxy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import me.anisekai.server.events.EntityCreatedEventAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class allowing to easily map an entity type and its repository to the {@link EventProxy} class.
 *
 * @param <E>
 *         Type of the entity.
 * @param <ID>
 *         Type of the entity's ID.
 * @param <I>
 *         Main interface the entity implements.
 * @param <R>
 *         Type  of the repository.
 */
public abstract class ProxyService<E extends I, ID extends Serializable, I extends Entity<ID>, R extends JpaRepository<E, ID>> {

    private final class ProxyBulk {

        private final Function<R, List<E>> selector;

        private final Map<ID, EventProxy<I, E>> proxyMap    = new HashMap<>();
        private final Collection<E>             entities    = new ArrayList<>();
        private final List<I>                   proxiedData = new ArrayList<>();

        private ProxyBulk(Function<R, List<E>> selector) {

            this.selector = selector;
            List<E> data = selector.apply(ProxyService.this.repository);

            for (E entity : data) {
                EventProxy<I, E> proxy = new EventProxyAdapter<>(entity);
                this.proxyMap.put(entity.getId(), proxy);
                this.entities.add(entity);
                this.proxiedData.add(proxy.startProxy());
            }
        }

        public <V> V batch(Function<List<I>, V> transformer) {

            V result = transformer.apply(this.proxiedData);
            this.commit();
            return result;
        }

        public List<E> batch(Consumer<List<I>> consumer) {

            consumer.accept(this.proxiedData);
            return this.commit();
        }

        public List<E> commit() {

            ProxyService.this.repository.saveAll(this.entities);
            List<E> refreshed = this.selector.apply(ProxyService.this.repository);

            refreshed.stream()
                     .filter(entity -> entity.getId() != null)
                     .filter(entity -> this.proxyMap.containsKey(entity.getId()))
                     .peek(entity -> this.proxyMap.get(entity.getId()).updateEventsEntity(entity))
                     .map(entity -> this.proxyMap.get(entity.getId()))
                     .map(EventProxy::getEvents)
                     .flatMap(Collection::stream)
                     .forEach(ProxyService.this.publisher::publishEvent);

            return refreshed;
        }

    }

    private final ApplicationEventPublisher publisher;
    private final R                         repository;
    private final Supplier<E>               initializer;

    public ProxyService(ApplicationEventPublisher publisher, R repository, Supplier<E> initializer) {

        this.publisher   = publisher;
        this.repository  = repository;
        this.initializer = initializer;
    }

    /**
     * Retrieve the {@link ApplicationEventPublisher} instance currently being used to send proxy events.
     *
     * @return An {@link ApplicationEventPublisher}.
     */
    public ApplicationEventPublisher getPublisher() {

        return this.publisher;
    }

    /**
     * Retrieve the {@link JpaRepository} instance currently being used to interact with the database.
     *
     * @return A {@link JpaRepository}
     */
    public R getRepository() {

        return this.repository;
    }

    /**
     * Retrieve an entity that has been updated or created using the consumer provided.
     *
     * @param id
     *         Id of the entity to "upsert"
     * @param eventCreator
     *         BiFunction to use to instantiate a creation event to publish
     * @param upsert
     *         Consumer used when an entity is created or updated
     *
     * @return An up-to-date entity, whether it has been created or only updated.
     */
    public E upsertEntity(ID id, BiFunction<Object, E, ? extends EntityCreatedEventAdapter<E>> eventCreator, Consumer<I> upsert) {

        return this.upsertEntity(id, eventCreator, upsert, upsert);
    }

    public E upsertEntity(Function<R, Optional<E>> retriever, BiFunction<Object, E, ? extends EntityCreatedEventAdapter<E>> eventCreator, Consumer<I> upsert) {

        return this.upsertEntity(retriever, eventCreator, upsert, upsert);
    }

    /**
     * Retrieve an entity that has been updated using the updater consumer or an entity created using the initializer if
     * no entity of the provided id was found.
     *
     * @param id
     *         Id of the entity to "upsert"
     * @param eventCreator
     *         BiFunction to use to instantiate a creation event to publish
     * @param initializer
     *         Consumer used when an entity is created
     * @param updater
     *         Consumer used when an entity is updated
     *
     * @return An up-to-date entity, whether it has been created or only updated.
     */
    public E upsertEntity(ID id, BiFunction<Object, E, ? extends EntityCreatedEventAdapter<E>> eventCreator, Consumer<I> initializer, Consumer<I> updater) {

        return this.upsertEntity(repo -> repo.findById(id), eventCreator, initializer, updater);
    }

    public E upsertEntity(Function<R, Optional<E>> retriever, BiFunction<Object, E, ? extends EntityCreatedEventAdapter<E>> eventCreator, Consumer<I> initializer, Consumer<I> updater) {

        return this.fetchEntity(retriever)
                   .map(entity -> this.modify(entity, updater))
                   .orElseGet(() -> this.create(initializer, eventCreator));
    }


    /**
     * Retrieve an optional entity from the provided repository selector.
     *
     * @param selector
     *         The selector to use to retrieve the entity.
     *
     * @return An optional entity, non-empty if the selector found a match.
     */
    public Optional<E> fetchEntity(Function<R, Optional<E>> selector) {

        return selector.apply(this.getRepository());
    }

    /**
     * Syntax-sugar method to use {@link #fetchEntities(Function)} using the id.
     *
     * @param id
     *         The id to use when selecting from the database.
     *
     * @return An optional entity, non-empty if the selector found a match.
     */
    public Optional<E> fetchEntity(ID id) {

        return this.fetchEntity(repository -> repository.findById(id));
    }

    /**
     * Retrieve all entities from the provided repository selector.
     *
     * @param selector
     *         The selector to use to retrieve all the entities.
     *
     * @return A list of entities, may be empty.
     */
    public List<E> fetchEntities(Function<R, List<E>> selector) {

        return selector.apply(this.getRepository());
    }

    /**
     * Same as {@link #fetchEntity(Function)} but should ensure that the selector should not return any empty optional
     * instance by throwing any {@link RuntimeException} using {@link Optional#orElseThrow(Supplier)}.
     *
     * @param selector
     *         The selector to use to retrieve the entity.
     *
     * @return The entity instance.
     */
    public abstract E getEntity(Function<R, Optional<E>> selector);

    /**
     * Syntax-sugar method to use {@link #getEntity(Function)} using the id.
     *
     * @param id
     *         The id to use when selecting from the database.
     *
     * @return The entity instance.
     */
    public E getEntity(ID id) {

        return this.getEntity(repository -> repository.findById(id));
    }

    /**
     * Create a new entity in this context and use the provided consumer to initialize that entity data, then use the
     * provided function to create the event associated to that entity creation.
     *
     * @param dataInitializer
     *         Consumer allowing to define the entity initial data.
     * @param eventCreator
     *         The function allowing to create the event to notify for the entity creation.
     *
     * @return The newly inserted entity instance
     */
    public E create(Consumer<I> dataInitializer, BiFunction<Object, E, ? extends EntityCreatedEventAdapter<E>> eventCreator) {

        E build = this.initializer.get();
        dataInitializer.accept(build);
        E saved = this.saveReload(build);
        this.getPublisher().publishEvent(eventCreator.apply(this, saved));
        return saved;
    }

    /**
     * Save the provided entity and reload it from the repository right after. This ensures every property annotated
     * with {@link ManyToMany} or {@link OneToMany} to properly be refreshed.
     * <p>
     * Unfortunately, this was impossible to do using {@link EntityManager#refresh(Object)} as this required a
     * transaction context which is not achievable using the Discord library.
     *
     * @param entity
     *         The entity to save then reload.
     *
     * @return The updated entity instance
     */
    public E saveReload(E entity) {

        E saved = this.repository.save(entity);
        Assert.notNull(saved.getId(), "Null id after save (??)");
        return this.getEntity(saved.getId());
    }

    /**
     * Start a proxy on the provided entity and use the modifier consumer provided to change the entity's properties and
     * send the associated events automatically.
     *
     * @param entity
     *         The entity to proxy.
     * @param mod
     *         The consumer that will receive the entity proxy.
     *
     * @return The updated entity instance
     */
    public E modify(E entity, Consumer<I> mod) {

        EventProxy<I, E> proxy         = new EventProxyAdapter<>(entity);
        I                proxiedEntity = proxy.startProxy();
        mod.accept(proxiedEntity);

        E refreshed = this.saveReload(entity);
        proxy.updateEventsEntity(refreshed);
        proxy.getEvents().forEach(this.publisher::publishEvent);

        return refreshed;
    }

    /**
     * Allow to modify the entity matching the provided id using the provided consumer.
     *
     * @param id
     *         The entity's ID.
     * @param mod
     *         The consumer allowing to modify the entity.
     *
     * @return The updated entity.
     *
     * @see #modify(Entity, Consumer)
     */
    public E modify(ID id, Consumer<I> mod) {

        return this.modify(this.getEntity(id), mod);
    }

    /**
     * Allow to modify the entity returned by the selector using the provided consumer.
     *
     * @param selector
     *         The selector used to retrieve a single entity.
     * @param mod
     *         The consumer allowing to modify the entity.
     *
     * @return The updated entity.
     *
     * @see #modify(Entity, Consumer)
     */
    public E modify(Function<R, Optional<E>> selector, Consumer<I> mod) {

        return this.modify(this.getEntity(selector), mod);
    }

    /**
     * Same as {@link #modify(Function, Consumer)} but for multiple entities at the same time.
     *
     * @param selector
     *         The selector used to retrieve multiple entities.
     * @param transformer
     *         The function allowing to modify each entity. The return value will be used as the return value for the
     *         method call.
     * @param <V>
     *         The type of the return value.
     *
     * @return The result of the transformer function
     */
    public <V> V batch(Function<R, List<E>> selector, Function<List<I>, V> transformer) {

        return new ProxyBulk(selector).batch(transformer);
    }

    /**
     * Same as {@link #modify(Function, Consumer)} but for multiple entities at the same time.
     *
     * @param selector
     *         The selector used to retrieve multiple entities.
     * @param consumer
     *         The function allowing to modify each entity.
     *
     * @return A list of updated entities.
     */
    public List<E> batch(Function<R, List<E>> selector, Consumer<List<I>> consumer) {

        return new ProxyBulk(selector).batch(consumer);
    }

    public List<E> batch(Iterable<ID> ids, Consumer<List<I>> consumer) {

        return this.batch(repository -> repository.findAllById(ids), consumer);
    }

    public UpsertResult<E> upsert(Function<R, Optional<E>> selector, Consumer<I> mod, BiFunction<Object, E, ? extends EntityCreatedEventAdapter<E>> eventCreator) {

        Optional<E> optional = selector.apply(this.getRepository());

        return optional.map(entity -> new UpsertResult<>(this.modify(entity, mod), UpsertAction.UPDATED))
                       .orElseGet(() -> new UpsertResult<>(this.create(mod, eventCreator), UpsertAction.INSERTED));
    }

}
