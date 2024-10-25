package me.anisekai.api.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Interface representing a persistable entity.
 *
 * @param <PK>
 *         Type of the primary key.
 */
public interface IEntity<PK extends Serializable> {

    /**
     * Retrieve this {@link IEntity} primary key.
     *
     * @return The primary key.
     */
    PK getId();

    /**
     * Define this {@link IEntity} primary key.
     *
     * @param id
     *         The primary key.
     */
    void setId(PK id);

    /**
     * Retrieve this {@link IEntity} creation date.
     *
     * @return The creation date.
     */
    ZonedDateTime getCreatedAt();

    /**
     * Define this {@link IEntity} creation date.
     *
     * @param createdAt
     *         The creation date.
     */
    void setCreatedAt(ZonedDateTime createdAt);

    /**
     * Retrieve this {@link IEntity} last update date.
     *
     * @return The last update date.
     */
    ZonedDateTime getUpdatedAt();

    /**
     * Define this {@link IEntity} last update date.
     *
     * @param updatedAt
     *         The last update date.
     */
    void setUpdatedAt(ZonedDateTime updatedAt);

    /**
     * Check if this {@link IEntity} has been persisted yet.
     *
     * @return True if not persisted, false otherwise.
     */
    boolean isNew();

}
