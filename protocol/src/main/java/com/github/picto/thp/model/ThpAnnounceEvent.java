package com.github.picto.thp.model;

/**
 * Enumeration of announce events.
 * Created by Pierre on 27/08/15.
 */
public enum ThpAnnounceEvent {
    /**
     * The first request to the tracker must include this event.
     */
    STARTED("started"),
    /**
     * Will be sent if the client is terminated gracefully, to drop all expectations from it.
     */
    STOPPED("stopped"),
    /**
     * Must be sent to the tracker when the download completes. Must not be sent if the download was already COMPLETED
     * when the client started. This is to allow the tracker to increment the "completed downloads" metric.
     */
    COMPLETED("completed"),

    /**
     * Is sent at regular interval to ask for more peers or update the tracker as to our progress.
     */
    REGULAR(null);

    private final String value;

    private ThpAnnounceEvent(final String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
