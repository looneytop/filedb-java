package ru.looneytop.filedb.model;

public enum Platform {
    PC(1),
    PLAYSTATION(2),
    XBOX(3),
    NINTENDO_SWITCH(4),
    MOBILE(5),
    OTHER(6);

    private final int code;

    Platform(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Platform fromCode(int code) {
        for (Platform platform : values()) {
            if (platform.code == code) {
                return platform;
            }
        }

        throw new IllegalArgumentException("Неизвестный код платформы: " + code);
    }
}
