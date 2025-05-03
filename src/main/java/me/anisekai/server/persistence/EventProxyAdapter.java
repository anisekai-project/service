package me.anisekai.server.persistence;

import fr.anisekai.wireless.api.persistence.EventProxyImpl;
import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import fr.anisekai.wireless.api.persistence.interfaces.EntityUpdatedEvent;

import java.lang.reflect.Constructor;

public class EventProxyAdapter<I extends Entity<?>, E extends I> extends EventProxyImpl<I, E> {

    public EventProxyAdapter(E instance) {

        super(instance);
    }

    @Override
    public EntityUpdatedEvent<E, ?> createEvent(Class<? extends EntityUpdatedEvent<?, ?>> eventType, E entity, Object oldValue, Object newValue) throws Exception {

        for (Constructor<?> constructor : eventType.getConstructors()) {
            if (constructor.getParameterCount() == 4) {
                Class<?>[] types = constructor.getParameterTypes();

                // types[0] is our event emitter in Springboot.
                boolean canAssignEntity   = types[1].isAssignableFrom(entity.getClass());
                boolean canAssignOldValue = types[2].isAssignableFrom(oldValue.getClass());
                boolean canAssignNewValue = types[3].isAssignableFrom(newValue.getClass());

                if (!canAssignEntity || !canAssignOldValue || !canAssignNewValue) {
                    continue;
                }

                return (EntityUpdatedEvent<E, ?>) constructor.newInstance(this, entity, oldValue, newValue);
            }
        }

        throw new IllegalStateException("Could not find any suitable constructor for event " + eventType.getSimpleName());
    }

}
