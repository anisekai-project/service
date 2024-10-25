package me.anisekai.api.persistence;

public record UpsertResult<T>(T result, boolean isNew) {

}
