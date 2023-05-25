package com.td.common;

public enum CommandType {
    QUIT,
    INDEX,
    GET,
    Q;

    public static CommandType getCommand(String received) {
        return valueOf(received.split(" ")[0].toUpperCase());
    }
}

