package me.anisekai.api.persistence.events;

import me.anisekai.api.persistence.IEntity;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class EntityDeletedEvent<T extends IEntity<?>> extends ApplicationEvent {

    private final T entity;

    public EntityDeletedEvent(Object source, T entity) {

        super(source);
        this.entity = entity;
    }

    public T getEntity() {

        return this.entity;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityDeletedEvent<?> that = (EntityDeletedEvent<?>) o;
        return Objects.equals(this.entity, that.entity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.entity);
    }

}
