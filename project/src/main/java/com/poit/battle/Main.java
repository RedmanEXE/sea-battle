package com.poit.battle;

import com.poit.battle.checkers.DataChecker;
import com.poit.battle.components.MapViewComponent;
import com.poit.battle.io.ConsoleUtils;
import com.poit.battle.io.KeyboardInput;
import com.poit.battle.models.Field;
import com.poit.battle.models.Player;
import com.poit.battle.models.Ship;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class Main {
    private static void playGame(@NotNull final KeyboardInput keyboardInput, @NotNull Player player1, @NotNull Player player2) {
        Player playerUnderFire = player2;
        Player firingPlayer = player1;
        int x, y;
        do {
            ConsoleUtils.clearConsole();
            System.out.println((firingPlayer.equals(player1) ? "Ход 1-го игрока\n" : "Ход 2-го игрока\n"));
            System.out.println("Поле противника:");
            playerUnderFire.getField().printField();
            System.out.println("\nВвод координат для выстрела:");
            y = keyboardInput.takeIntegerInRange("Строка: ", 1, 10) - 1;
            x = keyboardInput.takeIntegerInRange("Столбец: ", 1, 10) - 1;
            while (!playerUnderFire.getField().fire(x, y)) {
                ConsoleUtils.clearConsole();
                System.out.println((firingPlayer.equals(player1) ? "Ход 1-го игрока\n" : "Ход 2-го игрока\n"));
                System.out.println("Поле противника:");
                playerUnderFire.getField().printField();
                System.out.println("По этим координатам нельзя выстрелить, так как ранее по нём уже был проведён удар!\nВведите другие координаты:");
                y = keyboardInput.takeIntegerInRange("Строка: ", 1, 10) - 1;
                x = keyboardInput.takeIntegerInRange("Столбец: ", 1, 10) - 1;
            }

            if (Ship.isFiredShipBlock(playerUnderFire.getField().getBlock(x, y)))
                System.out.println("\nПопадание!\n");
            else {
                System.out.println("\nПромах!\n");
                playerUnderFire = (playerUnderFire.equals(player1) ? player2 : player1);
                firingPlayer = (firingPlayer.equals(player1) ? player2 : player1);
            }

            System.out.println(playerUnderFire.isGameOver()
                    ? "Нажмите [ENTER], чтобы перейти к результатам!"
                    : "Нажмите [ENTER], чтобы перейти к следующему ходу!");
            keyboardInput.holdInput();
        } while(!playerUnderFire.isGameOver());
        System.out.println((firingPlayer.equals(player1) ? "Победил игрок 1!" : "Победил игрок 2!"));
    }

    private static void initField(@NotNull final KeyboardInput keyboardInput, @NotNull final Field field, final int playerIndex) {
        while (!field.isInitialized()) {
            File player1File = keyboardInput.takeFile("Введите файл с картой для " + playerIndex + " игрока (.shf-файл): ", true,
                    (filepath) -> DataChecker.checkFileAvailability(filepath, true) && DataChecker.isShipsFile(filepath)
            );
            DataChecker.FieldCheckError checkError = field.initFromFile(player1File);
            if (checkError != DataChecker.FieldCheckError.NONE) {
                switch (checkError) {
                    case FILE_NOT_AVAILABLE -> System.out.println("Этот файл недоступен или занят другой программой! Введите путь до другого файла!");
                    case INVALID_DATA -> System.out.println("В этой карте содержатся неверные символы! Введите путь до другого файла!");
                    case NOT_ENOUGH_DATA -> System.out.println("В этом файле недостаточно данных! Введите путь до другого файла!");
                }
            }
        }
    }

    public static void main(String[] args) {
        KeyboardInput keyboardInput = new KeyboardInput();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Battle");
        frame.setSize(500, 500);
        MapViewComponent mapViewComponent = new MapViewComponent();
        frame.add(mapViewComponent);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        ConsoleUtils.clearConsole();
        System.out.println("Прототип игры морского боя\nНажмите [ENTER] для начала игры!");
        keyboardInput.holdInput();

        ConsoleUtils.clearConsole();
        System.out.println("Прототип игры морского боя\n");

        Field player1Field = new Field();
        Main.initField(keyboardInput, player1Field, 1);
        Player player1 = new Player(player1Field);
        mapViewComponent.setField(player1Field);
        mapViewComponent.repaint();

        Field player2Field = new Field();
        Main.initField(keyboardInput, player2Field, 2);
        Player player2 = new Player(player2Field);

        Main.playGame(keyboardInput, player1, player2);

        keyboardInput.holdInput();

        keyboardInput.close();
    }
}