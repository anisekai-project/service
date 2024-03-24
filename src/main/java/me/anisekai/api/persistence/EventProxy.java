package me.anisekai.api.persistence;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.globals.utils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class EventProxy<I extends IEntity<?>, E extends I> implements InvocationHandler {

    private final E                   entity;
    private final Map<Method, Method> methodCache;


    private final Map<Method, EntityUpdatedEvent<E, ?>> events;
    private final Map<Method, Object>                   valueCache;

    public EventProxy(E instance) {

        this.entity      = instance;
        this.methodCache = new HashMap<>();
        this.events      = new HashMap<>();
        this.valueCache  = new HashMap<>();


        Method[] methods = instance.getClass().getMethods();

        for (Method method : methods) {
            Method annotatedMethod = ReflectionUtils.findNearestWithAnnotation(method, TriggerEvent.class);
            if (annotatedMethod != null) {

                List<String> names = getPossibleGetterNames(instance.getClass(), method);
                Optional<Method> getter = Arrays.stream(instance.getClass().getMethods())
                                                .filter(m -> names.contains(m.getName()))
                                                .findFirst();

                if (getter.isEmpty()) {
                    throw new IllegalArgumentException(String.format(
                            "Cannot build EventProxy on %s: The method %s do not have a getter counterpart.",
                            instance.getClass().getName(),
                            method.getName()
                    ));
                }

                this.methodCache.put(annotatedMethod, getter.get());
            }
        }
    }

    @NotNull
    private static List<String> getPossibleGetterNames(Class<?> clazz, Method proxiedMethod) {

        if (!proxiedMethod.getName().startsWith("set")) {
            throw new IllegalArgumentException(String.format(
                    "Cannot build EventProxy on %s: The method %s is not a setter but is annotated with @TriggerEvent",
                    clazz.getName(),
                    proxiedMethod.getName()
            ));
        }

        String prop = proxiedMethod.getName().substring(3);

        return Arrays.asList(
                String.format("get%s", prop),
                String.format("is%s", prop),
                String.format("has%s", prop)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(TriggerEvent.class)) {

            if (!this.valueCache.containsKey(method)) {
                Method getter = this.methodCache.get(method);
                Object value  = getter.invoke(this.entity);
                this.valueCache.put(method, value);
            }

            Object previous = this.valueCache.get(method);
            Object next     = args[0];

            // We are safe with the next if
            Class<?> type = Optional.ofNullable(previous).orElse(next).getClass();

            if (Objects.equals(previous, next)) {
                // If they are the same, just rollback events
                this.events.remove(method);

            } else {
                TriggerEvent trigger = method.getAnnotation(TriggerEvent.class);

                this.events.put(method, (EntityUpdatedEvent<E, ?>) trigger
                        .value()
                        .getConstructor(
                                Object.class,
                                this.entity.getClass(),
                                type,
                                type
                        )
                        .newInstance(
                                this,
                                this.entity,
                                previous,
                                next
                        )
                );
            }
        }

        return method.invoke(this.entity, args);
    }

    public E getEntity() {

        return this.entity;
    }

    public List<EntityUpdatedEvent<E, ?>> getEvents() {

        return new ArrayList<>(this.events.values());
    }

    public void updateEventsEntity(E entity) {

        this.events.values().forEach(ev -> ev.setEntity(entity));
    }

    public I startProxy() {

        return (I) Proxy.newProxyInstance(
                this.entity.getClass().getClassLoader(),
                this.entity.getClass().getInterfaces(),
                this
        );
    }

}
