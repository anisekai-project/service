package me.anisekai.toshiko.enums;

public enum PublicationState {

    FINISHED(AnimeStatus.NOT_DOWNLOADED),
    AIRING(AnimeStatus.SIMULCAST_AVAILABLE),
    UNAVAILABLE(AnimeStatus.UNAVAILABLE);

    PublicationState(AnimeStatus status) {

    }

}
