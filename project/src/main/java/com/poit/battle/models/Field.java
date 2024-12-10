package com.poit.battle.models;

import com.poit.battle.checkers.DataChecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Класс для поля с картой в игре. Он выполняет следующие функции:<br>
 * - Содержит в себе полезные функции для самого процесса игры;<br>
 * - Позволяет инициализировать некоторые части автоматически;
 * <p>
 * Для создания верного объекта этого класса требуется объект класса {@link File},
 * который содержит путь до файла с картой. Этот файл нужно передать
 * в метод {@code Field.initFromFile(File)} после создания объекта с помощью конструктора.<br>
 * Если этого не сделать, то каждый метод, вызванный из этого объекта будет
 * бросать {@link FieldNotInitializedException}, что приведёт к вылету приложения!
 */
@SuppressWarnings("unused")
public class Field {
    // Внутренняя реализация карты в виде матрицы 10х10 из объектов блоков
    private final Block[][] map = new Block[10][10];
    // Внутренний сборщик карты из данных в матрице 10х10
    private static boolean buildMapFromData(final String[][] input, Field field) {
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                switch (input[x][y]) {
                    case "К", "K" -> field.map[x][y] = Block.SHIP;
                    case "М", "M" -> field.map[x][y] = Block.SEA;
                    default -> {
                        return false;
                    }
                }

        return true;
    }

    /**
     * Класс, хранящий типы клеток, которые могут быть размещены на карте
     */
    public enum Block {
        SEA,
        SHIP,
        MISSED,
        FIRED,
        KILLED,
    }

    /**
     * Стандартный конструктор для создания карты. Для полноценного использования
     * этого объекта требуется дополнительная инициализация с помощью метода
     * {@code Map.initFromFile(File)}
     */
    public Field() {}

    /**
     * Инициализирует объект с помощью информации из файла.
     * <p>
     * В случае, если файл содержит неверную информацию или происходит
     * любая другая ошибка при инициализации карты, возвращает соответствующий
     * код ошибки в виде объекта класса {@link com.poit.battle.checkers.DataChecker.FieldCheckError}
     *
     * @param file Файл, путь которого ведёт до данных с картой
     * @return Объект из класса {@link DataChecker.FieldCheckError} с ошибкой или {@code NONE}, если всё в порядке
     */
    public DataChecker.FieldCheckError initFromFile(final File file) {
        String[][] input = new String[10][10];
        String[] line;
        try (FileReader reader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(reader)) {
            for (int x = 0; x < 10; x++) {
                line = bufferedReader.readLine().split(" ");
                if (line.length < 10)
                    return DataChecker.FieldCheckError.NOT_ENOUGH_DATA;

                System.arraycopy(line, 0, input[x], 0, 10);
            }

            final DataChecker.FieldCheckError checkResult = DataChecker.isFieldProperly(input);
            if (checkResult == DataChecker.FieldCheckError.NONE) {
                if (!Field.buildMapFromData(input, this))
                    return DataChecker.FieldCheckError.INVALID_DATA;
            } else return checkResult;
        } catch (Exception e) {
            return DataChecker.FieldCheckError.FILE_NOT_AVAILABLE;
        }

        return DataChecker.FieldCheckError.NONE;
    }
}
