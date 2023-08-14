package me.anisekai.toshiko.events;

import me.anisekai.toshiko.interfaces.persistence.IEntity;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class EntityUpdatedEvent<E extends IEntity<?>, V> extends ApplicationEvent {

    private       E entity;
    private final V previous;
    private final V current;

    public EntityUpdatedEvent(Object source, E entity, V previous, V current) {

        super(source);
        this.entity   = entity;
        this.previous = previous;
        this.current  = current;
    }

    public E getEntity() {

        return this.entity;
    }

    /**
     * This should only be used to refresh entity instance after a save in a proxy. Should not be used outside this
     * context.
     *
     * @param entity
     *         The saved entity.
     */
    public void setEntity(E entity) {

        this.entity = entity;
    }

    public V getPrevious() {

        return this.previous;
    }

    public V getCurrent() {

        return this.current;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityUpdatedEvent<?, ?> that = (EntityUpdatedEvent<?, ?>) o;
        return Objects.equals(this.entity, that.entity) &&
                Objects.equals(this.previous, that.previous) &&
                Objects.equals(this.current, that.current);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.entity, this.previous, this.current);
    }

}
