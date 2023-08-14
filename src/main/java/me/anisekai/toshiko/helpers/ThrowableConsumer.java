package me.anisekai.toshiko.helpers;

public interface ThrowableConsumer<T> {

    void using(T t) throws Exception;

}
