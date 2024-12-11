package com.poit.battle.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ship {
    private Ship(final int size, final Direction direction) {
        this.size = size;
        this.direction = direction;
    }

    public final Direction direction;
    public final int size;
    public final int firedSize;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, SINGLE
    }

    @Nullable
    public static Ship find(@NotNull final Field.Block[][] map, final int x, final int y) {
        // TODO: Дописать поиск направления корабля и его размеров
        Direction direction = Direction.SINGLE;
        int size = 0;
        int firedSize = 0;

        if (!Ship.isShipBlock(map[x][y]))
            return null;

        for (int i = 0; i < 4; i++) {
            size++;
            if (Ship.isFiredShipBlock(map[x][y]))
                firedSize++;

            if (size == 0) {
                if (x > 0 && Ship.isShipBlock(map[x - 1][y]))
                    direction = Direction.LEFT;
                else if (x < 9 && Ship.isShipBlock(map[x + 1][y]))
                    direction = Direction.RIGHT;
                else if (y > 0 && Ship.isShipBlock(map[x][y - 1]))
                    direction = Direction.UP;
                else if (y < 9 && Ship.isShipBlock(map[x][y + 1]))
                    direction = Direction.DOWN;
                else
                    break;
            } else {
                switch (direction) {
                    case UP -> {
                        if ((x - i) > 0 && !Ship.isShipBlock(map[x - (i + 1)][y])
                    }
                }
            }
        }

        return new Ship(size, direction);
    }

    public static boolean isShipBlock(final Field.Block block) {
        return block == Field.Block.SHIP || block == Field.Block.FIRED || block == Field.Block.KILLED;
    }

    public static boolean isFiredShipBlock(final Field.Block block) {
        return block == Field.Block.FIRED || block == Field.Block.KILLED;
    }
}
