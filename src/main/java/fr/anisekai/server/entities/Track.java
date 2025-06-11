package fr.anisekai.server.entities;

import fr.anisekai.wireless.api.media.enums.Codec;
import fr.anisekai.wireless.remote.interfaces.TrackEntity;
import fr.anisekai.wireless.utils.EntityUtils;
import jakarta.persistence.*;
import fr.anisekai.server.entities.adapters.TrackEventAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Track implements TrackEventAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Episode episode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Codec codec;

    @Column(nullable = false)
    private boolean forced = false;

    @Column
    private String language;

    @Column(nullable = false)
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public @NotNull Episode getEpisode() {

        return this.episode;
    }

    @Override
    public void setEpisode(@NotNull Episode episode) {

        this.episode = episode;
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
    public @NotNull Codec getCodec() {

        return this.codec;
    }

    @Override
    public void setCodec(@NotNull Codec codec) {

        this.codec = codec;
    }

    @Override
    public @Nullable String getLanguage() {

        return this.language;
    }

    @Override
    public void setLanguage(String language) {

        this.language = language;
    }

    @Override
    public boolean isForced() {

        return this.forced;
    }

    @Override
    public void setForced(boolean forced) {

        this.forced = forced;
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

        if (o instanceof TrackEntity<?> track) return EntityUtils.equals(this, track);
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
