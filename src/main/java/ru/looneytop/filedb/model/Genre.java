package ru.looneytop.filedb.model;

public enum Genre {
    ACTION(1),
    ADVENTURE(2),
    CASUAL(3),
    INDIE(4),
    MASSIVELY_MULTIPLAYER(5),
    RPG(6),
    RACING(7),
    SIMULATION(8),
    SPORTS(9),
    STRATEGY(10),
    STRATEGY_RPG(11),
    ACTION_RPG(12),
    SURVIVAL(13),
    HORROR(14),
    VISUAL_NOVEL(15),
    PUZZLE(16),
    PLATFORMER(17),
    SHOOTER(18),
    CARD_AND_BOARD(19),
    FIGHTING(20),
    SANDBOX(21),
    ROGUE_LIKE(22);

    private final int code;

    Genre(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Genre fromCode(int code) {
        for (Genre genre : values()) {
            if (genre.code == code) {
                return genre;
            }
        }

        throw new IllegalArgumentException("Неизвестный код жанра: " + code);
    }
}
