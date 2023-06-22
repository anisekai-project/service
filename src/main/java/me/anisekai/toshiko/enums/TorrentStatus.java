package me.anisekai.toshiko.enums;

public enum TorrentStatus {

    STOPPED(0, false),
    VERIFY_QUEUED(1, false),
    VERIFYING(2, false),
    DOWNLOAD_QUEUED(3, false),
    DOWNLOADING(4, false),
    SEED_QUEUED(5, true),
    SEEDING(6, true);

    private final int value;
    private final boolean finished;

    TorrentStatus(int value, boolean finished) {

        this.value = value;
        this.finished = finished;
    }

    public int getValue() {

        return this.value;
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
