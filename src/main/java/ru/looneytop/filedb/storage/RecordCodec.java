package ru.looneytop.filedb.storage;

/**
 * Преобразование одной записи в слот фиксированного размера и обратно.
 * Буфер содержит одну запись, а не строки всей базы.
 *
 * @param <T> будущий тип записи выбранной предметной области
 */
public interface RecordCodec<T> {
    int recordSize();

    byte[] encode(T record);

    T decode(byte[] bytes);
}

