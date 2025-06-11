package me.anisekai.server.events;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.api.persistence.interfaces.EntityUpdatedEvent;
import org.springframework.context.ApplicationEvent;

public abstract class EntityUpdatedEventAdapter<T extends Entity<?>, V> extends ApplicationEvent implements EntityUpdatedEvent<T, V> {

    private       T entity;
    private final V oldValue;
    private final V newValue;

    public EntityUpdatedEventAdapter(Object source, T entity, V oldValue, V newValue) {

        super(source);
        this.entity   = entity;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public T getEntity() {

        return this.entity;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void setEntity(T entity) {

        this.entity = entity;
    }

    @Override
    public V getOldValue() {

        return this.oldValue;
    }

    @Override
    public V getNewValue() {

        return this.newValue;
    }

}
