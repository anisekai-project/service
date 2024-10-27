package me.anisekai.globals.tasking;

import jakarta.persistence.*;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.globals.types.JSONType;
import org.hibernate.annotations.Type;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Task implements IEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String factory;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskState state;

    @Type(JSONType.class)
    private BookshelfJson arguments;

    @Column(nullable = false)
    private long failureCount;

    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime completedAt;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    /**
     * Retrieve this {@link IEntity} primary key.
     *
     * @return The primary key.
     */
    @Override
    public Long getId() {

        return this.id;
    }

    /**
     * Define this {@link IEntity} primary key.
     *
     * @param id
     *         The primary key.
     */
    @Override
    public void setId(Long id) {

        this.id = id;
    }

    public String getFactory() {

        return this.factory;
    }

    public void setFactory(String factory) {

        this.factory = factory;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public TaskState getState() {

        return this.state;
    }

    public void setState(TaskState state) {

        this.state = state;
    }

    public BookshelfJson getArguments() {

        return this.arguments;
    }

    public void setArguments(BookshelfJson arguments) {

        this.arguments = arguments;
    }

    public long getFailureCount() {

        return this.failureCount;
    }

    public void setFailureCount(long failureCount) {

        this.failureCount = failureCount;
    }

    public ZonedDateTime getStartedAt() {

        return this.startedAt;
    }

    public void setStartedAt(ZonedDateTime startedAt) {

        this.startedAt = startedAt;
    }

    public ZonedDateTime getCompletedAt() {

        return this.completedAt;
    }

    public void setCompletedAt(ZonedDateTime completedAt) {

        this.completedAt = completedAt;
    }

    /**
     * Retrieve this {@link IEntity} creation date.
     *
     * @return The creation date.
     */
    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    /**
     * Define this {@link IEntity} creation date.
     *
     * @param createdAt
     *         The creation date.
     */
    @Override
    public void setCreatedAt(ZonedDateTime createdAt) {

        this.createdAt = createdAt;
    }

    /**
     * Retrieve this {@link IEntity} last update date.
     *
     * @return The last update date.
     */
    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    /**
     * Define this {@link IEntity} last update date.
     *
     * @param updatedAt
     *         The last update date.
     */
    @Override
    public void setUpdatedAt(ZonedDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isNew() {

        return this.id == null;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof Task other && EntityUtils.equals(this, other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.id);
    }

    @PrePersist
    private void beforePersist() {

        this.createdAt = this.createdAt.withZoneSameInstant(ZoneId.systemDefault());
        this.updatedAt = this.updatedAt.withZoneSameInstant(ZoneId.systemDefault());
    }

}
