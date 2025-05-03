package me.anisekai.server.entities;

import fr.anisekai.wireless.remote.interfaces.SettingEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import me.anisekai.server.entities.adapters.SettingEventAdapter;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Setting implements SettingEventAdapter {

    @Id
    private String id;

    @Column
    private String value;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public String getId() {

        return this.id;
    }

    @Override
    public void setId(String id) {

        this.id = id;
    }

    @Override
    public @Nullable String getValue() {

        return this.value;
    }

    @Override
    public void setValue(String value) {

        this.value = value;
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

        if (o instanceof SettingEntity setting) return EntityUtils.equals(this, setting);
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
