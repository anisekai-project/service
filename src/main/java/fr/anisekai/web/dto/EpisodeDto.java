package fr.anisekai.web.dto;

import fr.anisekai.server.entities.Episode;

public class EpisodeDto {

    public long id;
    public int  number;

    public EpisodeDto(Episode episode) {

        this.id     = episode.getId();
        this.number = episode.getNumber();
    }

    public String getName() {

        return String.format("Épisode %s", this.number);
    }

}
