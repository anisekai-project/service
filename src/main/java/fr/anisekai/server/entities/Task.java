package fr.anisekai.server.entities;

import fr.anisekai.server.entities.adapters.TaskEventAdapter;
import fr.anisekai.server.types.JSONType;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.remote.enums.TaskStatus;
import fr.anisekai.wireless.remote.interfaces.TaskEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Task implements TaskEventAdapter {

    public static final byte PRIORITY_DEFAULT        = 0;
    public static final byte PRIORITY_AUTOMATIC_LOW  = 1;
    public static final byte PRIORITY_MANUAL_LOW     = 2;
    public static final byte PRIORITY_AUTOMATIC_HIGH = 3;
    public static final byte PRIORITY_MANUAL_HIGH    = 4;
    public static final byte PRIORITY_URGENT         = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String factoryName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    private byte priority = 0;

    @Type(JSONType.class)
    private AnisekaiJson arguments;

    @Column(nullable = false)
    private byte failureCount;

    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime completedAt;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public @NotNull String getFactoryName() {

        return this.factoryName;
    }

    @Override
    public void setFactoryName(@NotNull String factoryName) {

        this.factoryName = factoryName;
    }

    @Override
    public @NotNull String getName() {

        return this.name;
    }

    @Override
    public void setName(@NotNull String name) {

        this.name = name;
    }

    @Override
    public @NotNull TaskStatus getStatus() {

        return this.status;
    }

    @Override
    public void setStatus(@NotNull TaskStatus status) {

        this.status = status;
    }

    @Override
    public byte getPriority() {

        return this.priority;
    }

    @Override
    public void setPriority(byte priority) {

        this.priority = priority;
    }

    @Override
    public @NotNull AnisekaiJson getArguments() {

        return this.arguments;
    }

    @Override
    public void setArguments(@NotNull AnisekaiJson arguments) {

        this.arguments = arguments;
    }

    @Override
    public byte getFailureCount() {

        return this.failureCount;
    }

    @Override
    public void setFailureCount(byte failureCount) {

        this.failureCount = failureCount;
    }

    @Override
    public @Nullable ZonedDateTime getStartedAt() {

        return this.startedAt;
    }

    @Override
    public void setStartedAt(ZonedDateTime startedAt) {

        this.startedAt = startedAt;
    }

    @Override
    public @Nullable ZonedDateTime getCompletedAt() {

        return this.completedAt;
    }

    @Override
    public void setCompletedAt(ZonedDateTime completedAt) {

        this.completedAt = completedAt;
    }

    @Override
    public ZonedDateTime getCreatedAt() {

        return this.createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {

        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof TaskEntity task) return EntityUtils.equals(this, task);
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

    public String toDiscordName() {

        return String.format(
                "Tâche **%s** (**%s** : `%s`)",
                this.getId(),
                this.getFactoryName(),
                this.getName()
        );
    }

}
