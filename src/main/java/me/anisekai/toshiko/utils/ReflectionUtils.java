package me.anisekai.toshiko.utils;

import jakarta.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ReflectionUtils {

    @Nullable
    public static <T extends Annotation> Method findNearestWithAnnotation(Method method, Class<T> annotationClass) {

        if (method.isAnnotationPresent(annotationClass)) {
            return method;
        }

        for (Class<?> anInterface : method.getDeclaringClass().getInterfaces()) {
            Method queryMethod = extractNearestAnnotation(anInterface, method, annotationClass);
            if (queryMethod != null) return queryMethod;
        }

        return null;
    }

    @Nullable
    public static <T extends Annotation> Method extractNearestAnnotation(Class<?> lookup, Method method, Class<T> annotationClass) {

        for (Method lookupMethod : lookup.getMethods()) {
            if (areSameMethods(lookupMethod, method) && lookupMethod.isAnnotationPresent(annotationClass)) {
                return lookupMethod;
            }
        }

        return null;
    }

    public static boolean areSameMethods(Method a, Method b) {

        if (!a.getName().equals(b.getName())) return false;
        if (!a.getReturnType().equals(b.getReturnType())) return false;
        if (a.getParameterCount() != b.getParameterCount()) return false;
        for (int i = 0; i < a.getParameterTypes().length; i++) {
            if (!a.getParameterTypes()[i].equals(b.getParameterTypes()[i])) return false;
        }
        // Safe to assume that they are the same.
        return true;
    }

}
