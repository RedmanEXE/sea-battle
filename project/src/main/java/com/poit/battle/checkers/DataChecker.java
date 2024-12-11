package com.poit.battle.checkers;

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

    /**
     * Проверяет, является ли карта и установленные на ней корабли верными.
     * Возвращает определённые ошибки, которые указывают на то, что неверно в выбранной карте!
     * В случае правильности файла возвращает {@code NONE}.
     *
     * @param input Файл, из которого нужно проверить данные. В случае, если {@code null} – метод вернёт {@code false}.
     * @return Объект из класса {@link FieldCheckError} с ошибкой или {@code NONE}, если всё в порядке
     */
    public static boolean isShipNearby(String[][] input, Boolean[][] checkMatrix, int i, int j) {

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};


        for (int dir = 0; dir < 8; dir++) {
            int ni = i + dx[dir];
            int nj = j + dy[dir];

            if (ni >= 0 && ni < 10 && nj >= 0 && nj < 10) {
                if (input[ni][nj].equals("K") && !checkMatrix[ni][nj]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int FindShip(String[][] input, Boolean[][] check, int i, int j) {
        int counter = 1;
        boolean findHor = false;
        boolean findVert = false;

        int[] collectOfShip = {0, 0, 0, 0};
        check[i][j] = true;

        for (int k = 1; k < 4; k++) {
            if (j + k < 10 && input[i][j + k].equals("K") && !findVert) {
                counter++;
                check[i][j + k] = true;
                findHor = true;
            } else if (i + k < 10 && input[i + k][j].equals("K") && !findHor) {
                counter++;
                check[i + k][j] = true;
                findVert = true;
            } else {
                break;
            }
        }
        return counter;
    }

    public static FieldCheckError processMatrix(String[][] input, Boolean[][] checkMatrix) {
        boolean shipNear;
        int[] collectOfShip = {0, 0, 0, 0};
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (input[i][j].equals("K") && !checkMatrix[i][j]) { // Если найдена часть корабля
                    collectOfShip[FindShip(input, checkMatrix, i, j)]++;
                    shipNear = isShipNearby(input, checkMatrix, i, j);
                    if (shipNear) return FieldCheckError.INVALID_DATA;
                }
            }
        }
        for (int i = 0; i < 4; i++){
            if (4 - i != collectOfShip[i]) return FieldCheckError.NOT_ENOUGH_DATA;
        }
        return FieldCheckError.NONE;
    }
    
    public static FieldCheckError isFieldProperly(final String[][] input) {
        // TODO: Реализовать статический метод для проверки файла на наличие верных данных
        Boolean[][] checkMatrix = new Boolean[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkMatrix[i][j] = false;
            }
        }
        codeError = processMatrix(input, checkMatrix);
        return codeError;
    }
}
