package me.anisekai.server.entities;

import jakarta.persistence.*;
import me.anisekai.api.persistence.EntityUtils;
import me.anisekai.server.enums.TrackType;
import me.anisekai.server.interfaces.ITrack;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Track implements ITrack<Media> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Media media;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrackType type;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public Long getId() {

        return this.id;
    }

    @Override
    public Media getMedia() {

        return this.media;
    }

    @Override
    public void setMedia(Media media) {

        this.media = media;
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
    public TrackType getType() {

        return this.type;
    }

    @Override
    public void setType(TrackType type) {

        this.type = type;
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

        if (o instanceof ITrack<?> track) return EntityUtils.equals(this, track);
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

    public File asFile(File root) {

        Media   media   = this.getMedia();
        Episode episode = media.getEpisode();
        Anime   anime   = episode.getAnime();

        File animeTarget   = new File(root, anime.getId().toString());
        File episodeTarget = new File(animeTarget, episode.getId().toString());
        File mediaTarget   = new File(episodeTarget, media.getId().toString());
        return new File(mediaTarget, this.getName() + this.getType().getExtension());
    }

}
