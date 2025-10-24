package com.roco.entity;

public enum LiveStreamStatus {
    CREATED(0, "Created"),
    LIVE(1, "Live"),
    STOPPED(2, "Stopped");

    private final int code;
    private final String description;

    LiveStreamStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}