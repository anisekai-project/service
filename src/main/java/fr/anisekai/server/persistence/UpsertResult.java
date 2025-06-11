package fr.anisekai.server.persistence;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;

public record UpsertResult<T extends Entity<?>>(T entity, UpsertAction action) {

}
