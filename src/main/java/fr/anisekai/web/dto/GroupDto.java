package fr.anisekai.web.dto;

import java.util.List;

public class GroupDto {

    public final String         name;
    public       List<AnimeDto> animes;

    public GroupDto(String name) {

        this.name = name;
    }

}
