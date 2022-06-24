package me.anisekai.toshiko.enums;

public enum PublicationState {

    UNKNOWN("Inconnu", AnimeStatus.NOT_DOWNLOADED),
    FINISHED("Diffusion terminée", AnimeStatus.NOT_DOWNLOADED),
    AIRING("Diffusion en cours", AnimeStatus.SIMULCAST_AVAILABLE),
    UNAVAILABLE("Non disponible", AnimeStatus.UNAVAILABLE);

    private final String label;
    private final AnimeStatus status;

    PublicationState(String label, AnimeStatus status) {

        this.label = label;
        this.status = status;
    }

    public String getLabel() {

        return this.label;
    }

    public AnimeStatus getStatus() {

        return this.status;
    }
}
