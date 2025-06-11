package fr.anisekai.server.events;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.api.persistence.interfaces.EntityEvent;
import org.springframework.context.ApplicationEvent;

public abstract class EntityDeletedEventAdapter<T extends Entity<?>> extends ApplicationEvent implements EntityEvent<T> {

    private T entity;

    public EntityDeletedEventAdapter(Object source, T entity) {

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
