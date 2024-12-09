package com.poit.battle.checkers;

import java.io.File;

/**
 * Класс для проверки данных в игре
 */
@SuppressWarnings("unused")
public class DataChecker {
    /**
     * Класс, в котором хранятся возможные ошибки, касающиеся проверяемой карты
     */
    public enum MapCheckError {
        // TODO: Добавить ошибки, которые будет возвращать метод для проверки правильности карты
        NONE,
    }

    /**
     * Проверяет, является ли карта и установленные на ней корабли верными.
     * Возвращает определённые ошибки, которые указывают на то, что неверно в выбранной карте!
     * В случае правильности файла возвращает {@code NONE}.
     *
     * @param input Файл, из которого нужно проверить данные. В случае, если {@code null} – метод вернёт {@code false}.
     * @return Объект из класса {@link MapCheckError} с ошибкой или {@code NONE}, если всё в порядке
     */
    public static MapCheckError isMapProperly(final File input) {
        // TODO: Реализовать статический метод для проверки файла на наличие верных данных
        return MapCheckError.NONE;
    }
}
