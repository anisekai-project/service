package me.anisekai.toshiko.enums;

public enum TorrentStatus {

    STOPPED(false),
    VERIFY_QUEUED(false),
    VERIFYING(false),
    DOWNLOAD_QUEUED(false),
    DOWNLOADING(false),
    SEED_QUEUED(true),
    SEEDING(true);

    private final boolean finished;

    TorrentStatus(boolean finished) {

        this.finished = finished;
    }

    public boolean isFinished() {

        return this.finished;
    }

    public static TorrentStatus from(int status) {

        return switch (status) {
            case 0 -> STOPPED;
            case 1 -> VERIFY_QUEUED;
            case 2 -> VERIFYING;
            case 3 -> DOWNLOAD_QUEUED;
            case 4 -> DOWNLOADING;
            case 5 -> SEED_QUEUED;
            case 6 -> SEEDING;
            default -> throw new IllegalArgumentException("The status provided is invalid.");
        };
    }
}
