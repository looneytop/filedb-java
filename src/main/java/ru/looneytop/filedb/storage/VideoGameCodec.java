package ru.looneytop.filedb.storage;

import ru.looneytop.filedb.model.Genre;
import ru.looneytop.filedb.model.Platform;
import ru.looneytop.filedb.model.ReleaseDate;
import ru.looneytop.filedb.model.VideoGame;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class VideoGameCodec implements RecordCodec<VideoGame> {
    private static final int TITLE_MAX_BYTES = 256;
    private static final int DEVELOPER_MAX_BYTES = 256;
    private static final int RECORD_SIZE = 569;

    @Override
    public int recordSize() {
        return RECORD_SIZE;
    }

    @Override
    public byte[] encode(VideoGame record) {
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);
        buffer.putLong(record.getId());

        writeString(buffer, record.getTitle(), TITLE_MAX_BYTES);
        writeString(buffer, record.getDeveloper(), DEVELOPER_MAX_BYTES);

        buffer.putInt(record.getGenre().getCode());
        buffer.putInt(record.getPlatform().getCode());

        buffer.putInt(record.getReleaseDate().year());
        buffer.putInt(record.getReleaseDate().month());
        buffer.putInt(record.getReleaseDate().day());

        buffer.putDouble(record.getPrice());
        buffer.putDouble(record.getRating());
        buffer.putInt(record.getPlaytimeHours());

        buffer.put((byte) (record.isCompleted() ? 1 : 0 ));

        return buffer.array();
    }

    @Override
    public VideoGame decode(byte[] bytes) {
        if (bytes == null || bytes.length != RECORD_SIZE) {
            throw new IllegalArgumentException(
                "Запись должна содержать ровно " + RECORD_SIZE + " байт"
            );
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        long id = buffer.getLong();
        String title = readString(buffer, TITLE_MAX_BYTES);
        String developer = readString(buffer, DEVELOPER_MAX_BYTES);
        Genre genre = Genre.fromCode(buffer.getInt());
        Platform platform = Platform.fromCode(buffer.getInt());
        ReleaseDate releaseDate = new ReleaseDate(
            buffer.getInt(),
            buffer.getInt(),
            buffer.getInt()
        );
        double price = buffer.getDouble();
        double rating = buffer.getDouble();
        int playtimeHours = buffer.getInt();
        byte completedCode = buffer.get();

        if (completedCode != 0 && completedCode != 1) {
            throw new IllegalArgumentException(
                "Некорректный код прохождения: " + completedCode + ". Ожидался 0 или 1"
            );
        }
        boolean completed = completedCode == 1;

        return new VideoGame(
            id, title, developer, genre, platform, releaseDate,
            price, rating, playtimeHours, completed
        );
    }

    private static void writeString(ByteBuffer buffer, String value, int maxBytes) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxBytes) {
            throw new IllegalArgumentException(
                "Строка занимает " + bytes.length + " байт. Допустимо не более " + maxBytes
            );
        }

        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.position(buffer.position() + maxBytes - bytes.length);
    }

    private static String readString(ByteBuffer buffer, int maxBytes) {
        int length = buffer.getInt();
        if (length < 0 || length > maxBytes) {
            throw new IllegalArgumentException(
                "Некорректная длина строки: " + length + ". Допустимо от 0 до " + maxBytes
            );
        }

        byte[] bytes = new byte[length];
        buffer.get(bytes);
        buffer.position(buffer.position() + maxBytes - length);

        return new String(bytes, StandardCharsets.UTF_8);
    }
}

