package ru.looneytop.filedb.storage;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class RecordFile<T> implements AutoCloseable {
    private final RandomAccessFile file;
    private final RecordCodec<T> codec;

    private static final int MAGIC = 0x47444231;
    private static final int VERSION = 1;
    private static final int HEADER_SIZE = 36;

    private long slotCount = 0;
    private long activeCount = 0;
    private long freeHead = -1;

    public RecordFile(String path, RecordCodec<T> codec) throws IOException {
        this.codec = codec;
        this.file = new RandomAccessFile(path, "rw");

        try {
            if (file.length() == 0) {
                writeHeader();
            } else {
                readHeader();
            }
        } catch (IOException | RuntimeException error) {
            try {
                file.close();
            } catch (IOException closeError) {
                error.addSuppressed(closeError);
            }
            throw error;
        }
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

    private void writeHeader() throws IOException {
        file.seek(0);

        file.writeInt(MAGIC);
        file.writeInt(VERSION);
        file.writeInt(codec.recordSize());

        file.writeLong(slotCount);
        file.writeLong(activeCount);
        file.writeLong(freeHead);
    }

    private void readHeader() throws IOException {
        if (file.length() < HEADER_SIZE) {
            throw new IOException("Файл повреждён: заголовок неполный");
        }

        file.seek(0);

        int storedMagic = file.readInt();
        int storedVersion = file.readInt();
        int storedRecordSize = file.readInt();

        if (storedMagic != MAGIC) {
            throw new IOException("Файл не является нашей базой данных");
        }
        if (storedVersion != VERSION) {
            throw new IOException("Неподдерживаемая версия формата БД");
        }
        if (storedRecordSize != codec.recordSize()) {
            throw new IOException("Размер записи не соответствует кодеку");
        }

        slotCount = file.readLong();
        activeCount = file.readLong();
        freeHead = file.readLong();

        if (slotCount < 0 || activeCount < 0 || activeCount > slotCount) {
            throw new IOException("Файл повреждён: некорректные счётчики записей");
        }
    }

    private int slotSize() {
        return 1 + Long.BYTES + codec.recordSize();
    }

    public long insert(T record) throws IOException {
        byte[] bytes = codec.encode(record);
        long offset = 0;
        long nextFree = 0;

        if (freeHead == -1) {
            offset = HEADER_SIZE + slotCount * slotSize();
        } else {
            offset = freeHead;
            file.seek(offset + 1);
            nextFree = file.readLong();
        }

        file.seek(offset);

        file.writeByte(1); // признак существования 1байт
        file.writeLong(-1L); // адрес следующего свободного слота 8байт
        file.write(bytes); // данные игры 569байт

        if (freeHead == -1) {
            slotCount++;
        } else {
            freeHead = nextFree;
        }
        activeCount++;

        writeHeader();
        return offset;
    }

    public T read(long offset) throws IOException {
        if (offset < HEADER_SIZE || (offset - HEADER_SIZE) % slotSize() != 0 ||
                            (offset - HEADER_SIZE) / slotSize() >= slotCount) {
            throw new IllegalArgumentException("Некорректный адрес записи: " + offset);
        }

        file.seek(offset);
        byte state = file.readByte();

        if (state == 0) {
            throw new IOException("Запись по адресу " + offset + " удалена");
        } else if (state < 0 || state > 1) {
            throw new IOException("Файл повреждён: неизвестный признак записи " + state);
        }

        file.readLong();

        byte[] bytes = new byte[codec.recordSize()];
        file.readFully(bytes);

        return codec.decode(bytes);
    }

    public void delete(long offset) throws IOException {
        if (offset < HEADER_SIZE || (offset - HEADER_SIZE) % slotSize() != 0 ||
                            (offset - HEADER_SIZE) / slotSize() >= slotCount) {
            throw new IllegalArgumentException("Некорректный адрес записи: " + offset);
        }

        file.seek(offset);
        byte state = file.readByte();

        if (state == 0) {
            throw new IOException("Запись по адресу " + offset + " удалена");
        } else if (state < 0 || state > 1) {
            throw new IOException("Файл повреждён: неизвестный признак записи " + state);
        }

        file.seek(offset);
        file.writeByte(0);
        file.writeLong(freeHead);

        freeHead = offset;
        activeCount--;
        writeHeader();
    }

    public void update(long offset, T record) throws IOException {
        if (offset < HEADER_SIZE || (offset - HEADER_SIZE) % slotSize() != 0 ||
                        (offset - HEADER_SIZE) / slotSize() >= slotCount) {
            throw new IllegalArgumentException("Некорректный адрес записи: " + offset);
        }

        byte[] bytes = codec.encode(record);

        file.seek(offset);
        byte state = file.readByte();

        if (state == 0) {
            throw new IOException("Запись по адресу " + offset + " удалена");
        } else if (state < 0 || state > 1) {
            throw new IOException("Файл повреждён: неизвестный признак записи " + state);
        }

        file.seek(offset + 1 + Long.BYTES);
        file.write(bytes);
    }
}









