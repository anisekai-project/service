package me.anisekai.server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.interfaces.ISetting;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Setting implements ISetting {

    @Id
    private String id;

    @Column(nullable = true)
    private String value;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public String getId() {

        return this.id;
    }

    @Override
    public String getValue() {

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

        if (o instanceof ISetting setting) return EntityUtils.equals(this, setting);
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
