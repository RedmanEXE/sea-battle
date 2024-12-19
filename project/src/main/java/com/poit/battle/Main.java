package com.poit.battle;

import com.poit.battle.checkers.DataChecker;
import com.poit.battle.components.MapViewComponent;
import com.poit.battle.io.KeyboardInput;
import com.poit.battle.models.Field;
import com.poit.battle.models.Player;
import com.poit.battle.models.Ship;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {
    private static JFrame frame;
    private static MapViewComponent mapViewComponent;
    static Player playerUnderFire, firingPlayer;
    static void playGame(@NotNull final KeyboardInput keyboardInput, @NotNull Player player1, @NotNull Player player2) {
        playerUnderFire = player2;
        firingPlayer = player1;

        JDialog dialog = new JDialog();
        JLabel label = new JLabel();

        JButton button = new JButton();
        button.setText("Дальше");
        button.addActionListener((e) -> {
            dialog.setVisible(false);
        });

        dialog.setTitle("Battle");
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.setSize(200, 100);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);
        dialog.add(button, BorderLayout.SOUTH);

        mapViewComponent.setField(playerUnderFire.getField());
        mapViewComponent.repaint();
        mapViewComponent.setFireListener((x, y) -> {
            if (!playerUnderFire.getField().fire(x - 1, y - 1))
                System.out.println("This block cannot be fired");
            else {
                mapViewComponent.selectBlock(-1, -1);
                label.setText(Ship.isFiredShipBlock(playerUnderFire.getField().getBlock(x - 1, y - 1)) ? "Попадание!" : "Промах!");
                dialog.setVisible(true);

                if (playerUnderFire.isGameOver()) {
                    JDialog gameOverDialog = new JDialog();
                    JLabel gameOver = new JLabel("Игра окончена!\nПобедил игрок " + (firingPlayer.equals(player1) ? "1" : "2"));

                    JButton gameOverButton = new JButton();

                    gameOverDialog.setTitle("Battle");
                    gameOverDialog.setModal(true);
                    gameOverDialog.setAlwaysOnTop(true);
                    gameOverDialog.setLocationRelativeTo(null);
                    gameOverDialog.setResizable(false);
                    gameOverDialog.setSize(300, 100);
                    gameOver.setVerticalAlignment(SwingConstants.CENTER);
                    gameOver.setHorizontalAlignment(SwingConstants.CENTER);
                    gameOverDialog.add(gameOver);

                    gameOverButton.setText("ОК");
                    gameOverButton.addActionListener((e) -> {
                        gameOverDialog.setVisible(false);
                    });
                    gameOverDialog.add(gameOverButton, BorderLayout.SOUTH);

                    gameOverDialog.setVisible(true);
                    return;
                }

                if (!Ship.isFiredShipBlock(playerUnderFire.getField().getBlock(x - 1, y - 1))) {
                    playerUnderFire = (playerUnderFire.equals(player1) ? player2 : player1);
                    firingPlayer = (firingPlayer.equals(player1) ? player2 : player1);
                    mapViewComponent.setField(playerUnderFire.getField());
                    mapViewComponent.repaint();
                }
            }
        });

        //int x, y;
//        while (true) {
//
//        }
//        do {
//            ConsoleUtils.clearConsole();
//            System.out.println((firingPlayer.equals(player1) ? "Ход 1-го игрока\n" : "Ход 2-го игрока\n"));
//            System.out.println("Поле противника:");
//            mapViewComponent.repaint();
//            System.out.println("\nВвод координат для выстрела:");
//
//            y = keyboardInput.takeIntegerInRange("Строка: ", 1, 10) - 1;
//            x = keyboardInput.takeIntegerInRange("Столбец: ", 1, 10) - 1;
//            while (!playerUnderFire.getField().fire(x, y)) {
//                ConsoleUtils.clearConsole();
//                System.out.println((firingPlayer.equals(player1) ? "Ход 1-го игрока\n" : "Ход 2-го игрока\n"));
//                System.out.println("Поле противника:");
//                mapViewComponent.repaint();
//                System.out.println("По этим координатам нельзя выстрелить, так как ранее по нём уже был проведён удар!\nВведите другие координаты:");
//                y = keyboardInput.takeIntegerInRange("Строка: ", 1, 10) - 1;
//                x = keyboardInput.takeIntegerInRange("Столбец: ", 1, 10) - 1;
//            }
//
//            if (Ship.isFiredShipBlock(playerUnderFire.getField().getBlock(x, y)))
//                System.out.println("\nПопадание!\n");
//            else {
//                System.out.println("\nПромах!\n");
//                playerUnderFire = (playerUnderFire.equals(player1) ? player2 : player1);
//                firingPlayer = (firingPlayer.equals(player1) ? player2 : player1);
//                mapViewComponent.setField(playerUnderFire.getField());
//            }
//
//            System.out.println(playerUnderFire.isGameOver()
//                    ? "Нажмите [ENTER], чтобы перейти к результатам!"
//                    : "Нажмите [ENTER], чтобы перейти к следующему ходу!");
//            keyboardInput.holdInput();
//        } while(!playerUnderFire.isGameOver());
//        System.out.println((firingPlayer.equals(player1) ? "Победил игрок 1!" : "Победил игрок 2!"));
    }

    static void initField(@NotNull final KeyboardInput keyboardInput, @NotNull final Field field, final int playerIndex) {
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
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Battle");
        frame.setSize(400, 500);
        mapViewComponent = new MapViewComponent();
        frame.add(mapViewComponent, BorderLayout.CENTER);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        mapViewComponent.setBounds(35, 0, 332, 332);

        JTextField pathForPlayer1Field = new JTextField();
        frame.add(pathForPlayer1Field, BorderLayout.SOUTH);
        pathForPlayer1Field.setSize(400, 30);
        pathForPlayer1Field.setEditable(true);
        pathForPlayer1Field.setVisible(true);

        JTextField pathForPlayer2Field = new JTextField();
        frame.add(pathForPlayer2Field, BorderLayout.SOUTH);
        pathForPlayer2Field.setSize(400, 30);
        pathForPlayer2Field.setBounds(
                frame.getWidth() - pathForPlayer2Field.getWidth(),
                frame.getHeight() - pathForPlayer2Field.getHeight(),
                pathForPlayer2Field.getWidth(),
                pathForPlayer2Field.getHeight()
        );
        pathForPlayer2Field.setEditable(true);
        pathForPlayer2Field.setVisible(true);

//        ConsoleUtils.clearConsole();
//        System.out.println("Прототип игры морского боя\nНажмите [ENTER] для начала игры!");
//        keyboardInput.holdInput();
//
//        ConsoleUtils.clearConsole();
//        System.out.println("Прототип игры морского боя\n");

        Field player1Field = new Field();
        Main.initField(keyboardInput, player1Field, 1);
        Player player1 = new Player(player1Field);

        Field player2Field = new Field();
        Main.initField(keyboardInput, player2Field, 2);
        Player player2 = new Player(player2Field);

        Main.playGame(keyboardInput, player1, player2);
    }
}