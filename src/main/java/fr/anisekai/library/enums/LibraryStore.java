package fr.anisekai.library.enums;

public enum LibraryStore {

    CHUNKS(true),
    SUBTITLE(true),
    TEMPORARY(false),
    DOWNLOAD(false),
    EVENT_IMAGES(false);

    private final boolean entityStore;

    LibraryStore(boolean entityStore) {this.entityStore = entityStore;}

    public boolean isEntityStore() {

        return this.entityStore;
    }

    public String getStoreName() {

        return switch (this) {
            case CHUNKS -> "chunks";
            case SUBTITLE -> "subs";
            case TEMPORARY -> "tmp";
            case DOWNLOAD -> "downloads";
            case EVENT_IMAGES -> "event-images";
        };
    }
}
