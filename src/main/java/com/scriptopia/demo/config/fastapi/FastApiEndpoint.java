package com.scriptopia.demo.config.fastapi;

public enum FastApiEndpoint {
    INIT("/games/init"),
    CHOICE("/games/choice"),
    BATTLE("/games/battle"),
    ITEM("/games/item"),
    DONE("/games/done");

    private final String path;

    FastApiEndpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}