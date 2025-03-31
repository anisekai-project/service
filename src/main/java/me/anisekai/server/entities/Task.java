package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.server.enums.TaskState;
import me.anisekai.server.interfaces.ITask;
import me.anisekai.server.types.JSONType;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Task implements ITask {

    public static final long PRIORITY_DEFAULT        = 0;
    public static final long PRIORITY_AUTOMATIC_LOW  = 1;
    public static final long PRIORITY_MANUAL_LOW     = 2;
    public static final long PRIORITY_AUTOMATIC_HIGH = 3;
    public static final long PRIORITY_MANUAL_HIGH    = 4;
    public static final long PRIORITY_URGENT         = 5;

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

    @Column(nullable = false)
    private long priority = 0;

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

    @Override
    public String getFactory() {

        return this.factory;
    }

    @Override
    public void setFactory(String factory) {

        this.factory = factory;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public TaskState getState() {

        return this.state;
    }

    @Override
    public void setState(TaskState state) {

        this.state = state;
    }

    @Override
    public long getPriority() {

        return this.priority;
    }

    @Override
    public void setPriority(long priority) {

        this.priority = priority;
    }

    @Override
    public BookshelfJson getArguments() {

        return this.arguments;
    }

    @Override
    public void setArguments(BookshelfJson arguments) {

        this.arguments = arguments;
    }

    @Override
    public long getFailureCount() {

        return this.failureCount;
    }

    @Override
    public void setFailureCount(long failureCount) {

        this.failureCount = failureCount;
    }

    @Override
    public ZonedDateTime getStartedAt() {

        return this.startedAt;
    }

    @Override
    public void setStartedAt(ZonedDateTime startedAt) {

        this.startedAt = startedAt;
    }

    @Override
    public ZonedDateTime getCompletedAt() {

        return this.completedAt;
    }

    @Override
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
     * Retrieve this {@link IEntity} last update date.
     *
     * @return The last update date.
     */
    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof ITask task) return EntityUtils.equals(this, task);
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.getId());
    }

    @PreUpdate
    public void beforeSave() {

        this.updatedAt = ZonedDateTime.now();
    }

}
