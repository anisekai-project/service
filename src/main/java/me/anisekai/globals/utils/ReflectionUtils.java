package me.anisekai.globals.utils;

import jakarta.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class ReflectionUtils {

    private ReflectionUtils() {}

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
            if (haveIdenticalSignatures(lookupMethod, method) && lookupMethod.isAnnotationPresent(annotationClass)) {
                return lookupMethod;
            }
        }

        return null;
    }

    /**
     * Determines if two methods have the same signature by comparing their names, return types, and parameter types.
     * This won't check the method body.
     *
     * @param a
     *         The first method
     * @param b
     *         The second method
     *
     * @return {@code true} if the method signatures are identical, {@code false} otherwise
     */
    public static boolean haveIdenticalSignatures(Method a, Method b) {

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
