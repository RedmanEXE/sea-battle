package com.poit.battle.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Класс для генерации информации о корабле путём анализа карты
 * <p>
 * Создать объект этого класса можно с помощью статических
 * методов, которые находятся в этом классе
 */
public record Ship(int size, int firedSize, int beginX, int beginY, Direction direction) {
    /**
     * Класс, хранящий типы направлений корабля
     */
    public enum Direction {
        VERTICAL,
        HORIZONTAL,
        SINGLE
    }

    /**
     * Генерирует объект класса {@link Ship} с информацией о корабле, который находится
     * на координате (X; Y), переданных в аргумент функции.
     * <p />
     * Данный метод выполняет проверку при поиске корабля. Если по координате (X; Y)
     * нет блока {@code Block.SHIP}, то метод вернёт {@code null}.
     * <p />
     * Этот метод также находит количество клеток, поражённых в ходе игры с помощью метода
     * {@code Ship#isFiredShipBlock(Block)}
     *
     * @param map Матрица, состоящая из {@link com.poit.battle.models.Field.Block}, для поиска
     *            информации о корабле
     * @param x Координата X клетки, на которой находится корабль для поиска информации
     * @param y Координата Y клетки, на которой находится корабль для поиска информации
     *
     * @return Объект класса {@link Ship} с информации о корабле, иначе {@code null},
     *         если корабль не найден
     */
    @Nullable
    public static Ship find(@NotNull final Field.Block[][] map, final int x, final int y) {
        Direction direction = Direction.SINGLE;
        int size = 1;
        int firedSize = 0; // Количество клеток, которые у корабля подбиты
        int beginX = x;
        int beginY = y;

        if (Ship.isShipBlock(map[x][y]))
            return null; // Если изначальная клетка не является кораблём, то нам нечего искать

        // Определяем направление корабля
        if ((y > 0 && Ship.isShipBlock(map[x][y - 1])) || (y < 9 && Ship.isShipBlock(map[x][y + 1])))
            direction = Direction.VERTICAL;
        else if ((x > 0 && Ship.isShipBlock(map[x - 1][y])) || (x < 9 && Ship.isShipBlock(map[x + 1][y])))
            direction = Direction.HORIZONTAL;

        // Если корабль единичный, то какой смысл нам что-то дальше считать :)
        if (direction == Direction.SINGLE)
            return new Ship(size, Ship.isFiredShipBlock(map[x][y]) ? 1 : 0, beginX, beginY, direction);

        // Вычисляем нулевые координаты корабля, то есть его начало относительно осей
        while (beginX > 0 && beginY > 0)
            if (direction == Direction.VERTICAL) {
                if (Ship.isShipBlock(map[beginX][beginY - 1]))
                    beginY--;
                else
                    break;
            } else {
                if (Ship.isShipBlock(map[beginX - 1][beginY]))
                    beginX--;
                else
                    break;
            }

        // Вычисляем размер корабля, зная его начальные координаты и направление
        int tempX = beginX;
        int tempY = beginY;
        while (tempX < 9 && tempY < 9) {
            if (direction == Direction.VERTICAL) {
                if (Ship.isShipBlock(map[tempX][tempY + 1])) {
                    size++;
                    if (Ship.isFiredShipBlock(map[tempX][tempY + 1]))
                        firedSize++;
                } else
                    break;
                tempY++;
            } else {
                if (Ship.isShipBlock(map[tempX + 1][tempY])) {
                    size++;
                    if (Ship.isFiredShipBlock(map[tempX + 1][tempY]))
                        firedSize++;
                } else
                    break;
                tempX++;
            }
        }

        return new Ship(size, firedSize, beginX, beginY, direction);
    }

    /**
     * Проверяет, является ли переданный блок кораблём (то есть является
     * {@code Block.SHIP}, {@code Block.FIRED} или {@code Block.KILLED} )
     *
     * @param block Блок, который нужно проверить на наличие корабля
     *
     * @return {@code true}, если является кораблём, {@code false} в противном случае
     */
    public static boolean isShipBlock(final Field.Block block) {
        return block == Field.Block.SHIP || block == Field.Block.FIRED || block == Field.Block.KILLED;
    }

    /**
     * Проверяет, является ли переданный блок подбитым кораблём (то есть
     * является {@code Block.FIRED} или {@code Block.KILLED} )
     *
     * @param block Блок, который нужно проверить на наличие подбитого корабля
     *
     * @return {@code true}, если является подбитым кораблём,
     *         {@code false} в противном случае
     */
    public static boolean isFiredShipBlock(final Field.Block block) {
        return block == Field.Block.FIRED || block == Field.Block.KILLED;
    }

    /**
     * Проверяет, является ли переданный блок прострелянным (то есть
     * является {@code Block.MISSED}, {@code Block.FIRED} или {@code Block.KILLED} )
     *
     * @param block Блок, который нужно проверить на наличие прострела
     *
     * @return {@code true}, если является прострелянным блоком,
     *         {@code false} в противном случае
     */
    public static boolean isFiredBlock(final Field.Block block) {
        return block == Field.Block.MISSED || block == Field.Block.FIRED || block == Field.Block.KILLED;
    }
}
