package com.example.mediai.data.remote;

public enum AiProvider {
    MISTRAL,
    DEEPSEEK,
    DEMO;

    public static AiProvider fromString(String value) {
        if (value == null) return DEMO;
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEMO;
        }
    }
}