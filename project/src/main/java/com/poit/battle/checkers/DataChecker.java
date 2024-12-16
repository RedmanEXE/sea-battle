package com.poit.battle.checkers;

import java.io.*;

/**
 * Класс для проверки данных в игре
 */
@SuppressWarnings("unused")
public class DataChecker {
    /**
     * Класс, в котором хранятся возможные ошибки, касающиеся проверяемой карты
     */
    public enum FieldCheckError {
        // TODO: Добавить ошибки, которые будет возвращать метод для проверки правильности карты
        NONE,
        FILE_NOT_AVAILABLE,
        INVALID_DATA,
        NOT_ENOUGH_DATA,
    }

    private static boolean isShipNearby(String[][] input, boolean[][] checkMatrix, int i, int j) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int dir = 0; dir < 8; dir++) {
            int ni = i + dx[dir];
            int nj = j + dy[dir];

            if (ni >= 0 && ni < 10 && nj >= 0 && nj < 10) {
                if ((input[ni][nj].equals("K") || input[ni][nj].equals("К")) && !checkMatrix[ni][nj]) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int findShip(String[][] input, boolean[][] check, int i, int j) {
        int counter = 1;
        boolean findHor = false;
        boolean findVert = false;

        check[i][j] = true;

        for (int k = 1; k < 4; k++) {
            if (j + k < 10 && (input[i][j + k].equals("K") || input[i][j + k].equals("К")) && !findVert) {
                counter++;
                check[i][j + k] = true;
                findHor = true;
            } else if (i + k < 10 && (input[i + k][j].equals("K") || input[i + k][j].equals("К")) && !findHor) {
                counter++;
                check[i + k][j] = true;
                findVert = true;
            } else {
                break;
            }
        }
        return counter;
    }

    private static FieldCheckError processMatrix(String[][] input, boolean[][] checkMatrix) {
        boolean shipNear;
        int[] collectOfShip = {0, 0, 0, 0};
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((input[i][j].equals("K") || input[i][j].equals("К")) && !checkMatrix[i][j]) {
                    int val = DataChecker.findShip(input, checkMatrix, i, j); // Если найдена часть корабля
                    if (val > 0)
                        collectOfShip[val - 1]++;
                    shipNear = DataChecker.isShipNearby(input, checkMatrix, i, j);
                    if (shipNear) return FieldCheckError.INVALID_DATA;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (4 - i != collectOfShip[i]) return FieldCheckError.NOT_ENOUGH_DATA;
        }
        return FieldCheckError.NONE;
    }

    /**
     * Проверяет, является ли карта и установленные на ней корабли верными.
     * Возвращает определённые ошибки, которые указывают на то, что неверно в выбранной карте!
     * В случае правильности файла возвращает {@code NONE}.
     *
     * @param input Матрица с данными, из которой нужно проверить поле. В случае, если {@code null} – метод вернёт {@code false}.
     * @return Объект из класса {@link FieldCheckError} с ошибкой или {@code NONE}, если всё в порядке
     */
    public static FieldCheckError isFieldProperly(final String[][] input) {
        // TODO: Реализовать статический метод для проверки входа на наличие верных данных
        FieldCheckError error;
        boolean[][] checkMatrix = new boolean[10][10];
        error = processMatrix(input, checkMatrix);
        return error;
    }

    /**
     * Проверяет, является ли тип файл картой для игры.
     * <p />
     * Файлы карт для игры имеют расширение {@code .shf}.
     *
     * @param filePath Путь до проверяемого файла
     * @return {@code true}, если файл имеет требуемое расширение, {@code false}, если нет
     */
    public static boolean isShipsFile(String filePath) {
        return filePath.endsWith(".shf");
    }

    /**
     * Проверяет, является ли файл доступным в требуемом режиме.
     * <p />
     * Проверка происходит с помощью открытия файл для чтения и записи,
     * а после любой доступ к файлу закрывается.
     *
     * @param filePath Путь до проверяемого файла
     * @param read Параметр для требуемого режима проверки: {@code true}, если требуется
     *             проверка файла на доступность для чтения, {@code false}, если для записи
     * @return {@code true}, если файл доступен в требуемом режиме; {@code false}, если нет
     */
    public static boolean checkFileAvailability(final String filePath, final boolean read) {
        boolean fl;
        fl = true;
        try {
            File file = new File(filePath);
            if (!read) {
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
                fileWriter.close();
            } else {
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                fileReader.close();
            }
        } catch (Exception e) {
            fl = false;
        }
        return fl;
    }
}
