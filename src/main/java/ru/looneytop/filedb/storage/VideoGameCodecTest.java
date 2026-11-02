package ru.looneytop.filedb.storage;

import ru.looneytop.filedb.model.Genre;
import ru.looneytop.filedb.model.Platform;
import ru.looneytop.filedb.model.ReleaseDate;
import ru.looneytop.filedb.model.VideoGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VideoGameCodecTest {
    public static void main(String[] args) throws IOException {
        VideoGame first = new VideoGame(
            1L, "Ведьмак 3", "CD Projekt RED",
            Genre.RPG, Platform.PC,
            new ReleaseDate(2015, 5, 19),
            1999.90, 9.5, 120, true
        );

        VideoGame updated = new VideoGame(
            1L, "Ведьмак 3: Дикая Охота", "CD Projekt RED",
            Genre.RPG, Platform.PC,
            new ReleaseDate(2015, 5, 19),
            999.90, 10.0, 200, false
        );

        VideoGameCodec codec = new VideoGameCodec();
        Path path = Files.createTempFile("filedb-test-", ".db");

        try {
            long offset;

            try (RecordFile<VideoGame> db =
                     new RecordFile<>(path.toString(), codec)) {

                offset = db.insert(first);
                check(offset == 36, "Неверный адрес первой записи");
                checkGame(first, db.read(offset));
                System.out.println("Добавление и чтение — OK");

                long initialSize = Files.size(path);

                db.update(offset, updated);
                checkGame(updated, db.read(offset));
                check(Files.size(path) == initialSize,
                    "После редактирования изменился размер файла");
                System.out.println("Редактирование — OK");

                db.delete(offset);

                boolean readFailed = false;
                try {
                    db.read(offset);
                } catch (IOException expected) {
                    readFailed = true;
                }
                check(readFailed, "Удалённая запись осталась доступной");

                long reusedOffset = db.insert(first);
                check(reusedOffset == offset,
                    "Свободный слот не использован повторно");
                check(Files.size(path) == initialSize,
                    "При повторном использовании слота файл вырос");
                checkGame(first, db.read(reusedOffset));
                System.out.println("Удаление и повторное использование — OK");
            }

            // Предыдущий объект закрыт. Открываем файл заново.
            try (RecordFile<VideoGame> db =
                     new RecordFile<>(path.toString(), codec)) {

                checkGame(first, db.read(offset));

                long secondOffset = db.insert(updated);
                check(secondOffset == 614,
                    "После открытия неверно восстановлен счётчик слотов");
                checkGame(updated, db.read(secondOffset));
                checkGame(first, db.read(offset));

                System.out.println("Повторное открытие и добавление — OK");
            }

            System.out.println("Все проверки пройдены");
        } finally {
            Files.deleteIfExists(path);
        }
    }

    private static void checkGame(VideoGame expected, VideoGame actual) {
        boolean same =
            expected.getId() == actual.getId()
                && expected.getTitle().equals(actual.getTitle())
                && expected.getDeveloper().equals(actual.getDeveloper())
                && expected.getGenre() == actual.getGenre()
                && expected.getPlatform() == actual.getPlatform()
                && expected.getReleaseDate().equals(actual.getReleaseDate())
                && expected.getPrice() == actual.getPrice()
                && expected.getRating() == actual.getRating()
                && expected.getPlaytimeHours() == actual.getPlaytimeHours()
                && expected.isCompleted() == actual.isCompleted();

        check(same, "Прочитанная игра отличается от ожидаемой");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
