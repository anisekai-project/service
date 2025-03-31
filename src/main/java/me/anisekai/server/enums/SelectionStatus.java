package me.anisekai.server.enums;

public enum SelectionStatus {

    OPEN(false), CLOSED(true), AUTO_CLOSED(true);

    private final boolean closed;

    SelectionStatus(boolean closed) {

        this.closed = closed;
    }

    public boolean isClosed() {

        return this.closed;
    }
}
