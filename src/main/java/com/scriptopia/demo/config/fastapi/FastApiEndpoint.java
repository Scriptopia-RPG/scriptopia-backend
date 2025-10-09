package com.scriptopia.demo.config.fastapi;

public enum FastApiEndpoint {
    INIT("/games/init"),
    CHOICE("/games/choice"),
    BATTLE("/games/battle"),
    ITEM("/games/item"),
    DONE("/games/done"),
    END("/games/end"),
    TITLE("/games/title");

    private final String path;

    FastApiEndpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}