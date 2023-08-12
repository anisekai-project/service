package me.anisekai.toshiko.helpers;

public record UpsertResult<T>(T result, boolean isNew) {

}
