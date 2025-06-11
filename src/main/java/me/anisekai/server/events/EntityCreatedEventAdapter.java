package me.anisekai.server.events;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.api.persistence.interfaces.EntityEvent;
import org.springframework.context.ApplicationEvent;

public abstract class EntityCreatedEventAdapter<T extends Entity<?>> extends ApplicationEvent implements EntityEvent<T> {

    private T entity;

    public EntityCreatedEventAdapter(Object source, T entity) {

        super(source);
        this.entity = entity;
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

}
