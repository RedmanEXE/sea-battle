package com.poit.battle.models;

import com.poit.battle.checkers.DataChecker;
import org.jetbrains.annotations.Nullable;

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
    private boolean isInitialized = false;
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
     * Помечает клетку на поле как обстрелянную. У этого метода есть два поведения:<br />
     * - Если клетка была {@code Block.SEA}, то она превращается в {@code Block.MISSED}, и считается, что игрок промахнулся;<br />
     * - Если клетка была {@code Block.SHIP}, то она превращается в {@code Block.FIRED}, и считается, что игрок попал.
     * <p />
     * Кроме того метод автоматически проверяет следующую ситуацию:<br />
     * Если метод обнаруживает ряд клеток {@code Block.FIRED} (то есть корабль полностью подбит),
     * то он автоматически превращает этот ряд в {@code Block.KILLED}.
     * <p />
     * Метод также автоматически проверяет, чтобы клетка не была обстреляна.
     * В случае, если выстрел обнаружен, то метод вернёт {@code false}.
     * <p />
     * <b>Этот метод требует полностью инициализированную карту!</b>
     *
     * @param x Координата X клетки, которую необходимо пометить обстрелянной
     * @param y Координата Y клетки, которую необходимо пометить обстрелянной
     * @return {@code false}, если клетка не может быть помечена обстрелянной, в противном случае {@code true}
     * @throws FieldNotInitializedException если метод вызывается из объекта, который не был до конца инициализирован
     */
    public boolean fire(final int x, final int y) throws FieldNotInitializedException {
        if (!this.isInitialized())
            throw new FieldNotInitializedException("Field object has not been initialized");

        if (this.map[y][x] == Block.KILLED || this.map[y][x] == Block.MISSED || this.map[y][x] == Block.FIRED)
            return false;

        if (this.map[y][x] == Block.SHIP) {
            this.map[y][x] = Block.FIRED;
            Ship ship = Ship.find(this.map, x, y);
            if (ship != null && ship.size() == ship.firedSize()) {
                if (ship.direction() == Ship.Direction.VERTICAL)
                    for (int i = 0; i < ship.size(); i++)
                        this.map[ship.beginY() + i][x] = Block.KILLED;
                else if (ship.direction() == Ship.Direction.HORIZONTAL)
                    for (int i = 0; i < ship.size(); i++)
                        this.map[y][ship.beginX() + i] = Block.KILLED;
                else
                    this.map[y][x] = Block.KILLED;
            }
        } else if (this.map[y][x] == Block.SEA)
            this.map[y][x] = Block.MISSED;

        return true;
    }

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
    public final DataChecker.FieldCheckError initFromFile(final File file) {
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
                else
                    isInitialized = true;
            } else return checkResult;
        } catch (Exception e) {
            return DataChecker.FieldCheckError.FILE_NOT_AVAILABLE;
        }

        return DataChecker.FieldCheckError.NONE;
    }

    /**
     * Возвращает значение в зависимости от того, инициализировано
     * ли игровое поле
     *
     * @return {@code true}, если объект инициализирован, {@code false} в обратном случае
     */
    public final boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Возвращает блок по координатам X и Y из игрового поля
     *
     * @param x Координата X клетки, которую необходимо вернуть
     * @param y Координата Y клетки, которую необходимо вернуть
     * @return Объект класса {@link Block}, который находится по этим координатам
     */
    @Nullable
    public final Block getBlock(final int x, final int y) {
        if (x < 0 || x >= 10 || y < 0 || y >= 10)
            return null;

        return map[y][x];
    }

    // TODO: Удалить этот метод, когда будет переход на графический интерфейс
    public final void printField() throws FieldNotInitializedException {
        if (!this.isInitialized())
            throw new FieldNotInitializedException("Field object has not been initialized");

        final int LETTER_A_ASCII = Character.getNumericValue('А');
        System.out.println("   А Б В Г Д Е Ж З И К");
        for(int i = 0; i < 10; i++) {
            if (i + 1 < 10)
                System.out.print((i + 1) + "  ");
            else
                System.out.print((i + 1) + " ");
            for(int j = 0; j < 10; j++)
                switch (map[i][j]) {
                    case SHIP, SEA -> System.out.print("  ");
                    case FIRED ->  System.out.print("Р ");
                    case KILLED -> System.out.print("У ");
                    case MISSED -> System.out.print(". ");
                }
            System.out.println();
        }

    }
}

