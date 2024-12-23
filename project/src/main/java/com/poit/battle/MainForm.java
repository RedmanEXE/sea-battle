package com.poit.battle;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.poit.battle.checkers.DataChecker;
import com.poit.battle.components.MapViewComponent;
import com.poit.battle.models.Field;
import com.poit.battle.models.Player;
import com.poit.battle.models.Ship;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.List;

public class MainForm {
    private JTextField pathEditPlayer1;
    private JTextField pathEditPlayer2;
    private JPanel contentPane;
    private JButton playButton;
    private MapViewComponent mapViewComponent;
    private JLabel pathLabelPlayer1;
    private JLabel pathLabelPlayer2;
    private JLabel mapLabelTitle;
    private JLabel labelDescription;
    private JTextField coordsEdit;
    private JLabel coordsLabel;
    private JButton coordsFireButton;
    private JFrame frame;

    private final Field player1Field = new Field();
    private final Field player2Field = new Field();
    private Player playerUnderFire, firingPlayer;

    private static final String[] numbers = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static final String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    private static final HashSet<Character> allowedChars = new HashSet<>(
            List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ')
    );

    public static void main(String[] args) {
        MainForm mainForm = new MainForm();
        JDialog fileReadErrorDialog = new JDialog();
        JLabel fileReadErrorLabel = new JLabel();

        JButton fileReadErrorButton = new JButton();
        fileReadErrorButton.setText("ОК");
        fileReadErrorButton.addActionListener((e) -> {
            fileReadErrorDialog.setVisible(false);
        });

        fileReadErrorDialog.setTitle("Ошибка чтения файлов");
        fileReadErrorDialog.setModal(true);
        fileReadErrorDialog.setAlwaysOnTop(true);
        fileReadErrorDialog.setLocationRelativeTo(null);
        fileReadErrorDialog.setResizable(false);
        fileReadErrorDialog.setSize(450, 100);
        fileReadErrorLabel.setVerticalAlignment(SwingConstants.CENTER);
        fileReadErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fileReadErrorDialog.add(fileReadErrorLabel);
        fileReadErrorDialog.add(fileReadErrorButton, BorderLayout.SOUTH);

        mainForm.frame = new JFrame("Морской бой");
        mainForm.playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    File mapForPlayer1 = new File(mainForm.pathEditPlayer1.getText()), mapForPlayer2 = new File(mainForm.pathEditPlayer2.getText());

                    if (!DataChecker.isShipsFile(mainForm.pathEditPlayer1.getText()) ||
                            !DataChecker.checkFileAvailability(mainForm.pathEditPlayer1.getText(), true)) {
                        fileReadErrorLabel.setText("Файл 1 игрока недоступен для чтения или не является .shf-файлом!");
                        fileReadErrorDialog.setVisible(true);
                        return;
                    } else {
                        DataChecker.FieldCheckError checkError = mainForm.player1Field.initFromFile(mapForPlayer1);
                        switch (checkError) {
                            case INVALID_DATA ->
                                fileReadErrorLabel.setText("Файл поля 1 игрока содержит неверные данные!");
                            case FILE_NOT_AVAILABLE ->
                                fileReadErrorLabel.setText("Файл 1 игрока недоступен для чтения!");
                            case NOT_ENOUGH_DATA ->
                                fileReadErrorLabel.setText("Файл 1 игрока имеет недостаточно данных!");
                        }

                        if (checkError != DataChecker.FieldCheckError.NONE) {
                            fileReadErrorDialog.setVisible(true);
                            return;
                        }
                    }

                    if (!DataChecker.isShipsFile(mainForm.pathEditPlayer2.getText()) ||
                            !DataChecker.checkFileAvailability(mainForm.pathEditPlayer2.getText(), true)) {
                        fileReadErrorLabel.setText("Файл 2 игрока недоступен для чтения или не является .shf-файлом!");
                        fileReadErrorDialog.setVisible(true);
                        return;
                    } else {
                        DataChecker.FieldCheckError checkError = mainForm.player2Field.initFromFile(mapForPlayer2);
                        switch (checkError) {
                            case INVALID_DATA ->
                                    fileReadErrorLabel.setText("Файл 2 игрока содержит неверные данные!");
                            case FILE_NOT_AVAILABLE ->
                                    fileReadErrorLabel.setText("Файл 2 игрока недоступен для чтения!");
                            case NOT_ENOUGH_DATA ->
                                    fileReadErrorLabel.setText("Файл 2 игрока имеет недостаточно данных!");
                        }

                        if (checkError != DataChecker.FieldCheckError.NONE) {
                            fileReadErrorDialog.setVisible(true);
                            return;
                        }
                    }

                    Player player1 = new Player(mainForm.player1Field, 1);
                    Player player2 = new Player(mainForm.player2Field, 2);

                    mainForm.pathEditPlayer1.setVisible(false);
                    mainForm.pathEditPlayer2.setVisible(false);
                    mainForm.pathLabelPlayer1.setVisible(false);
                    mainForm.pathLabelPlayer2.setVisible(false);
                    mainForm.playButton.setVisible(false);
                    mainForm.mapViewComponent.setVisible(true);
                    mainForm.mapLabelTitle.setVisible(true);
                    mainForm.coordsEdit.setVisible(true);
                    mainForm.coordsEdit.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            mainForm.checkDataInCoordsEdit();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            mainForm.checkDataInCoordsEdit();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            mainForm.checkDataInCoordsEdit();
                        }
                    });
                    mainForm.coordsLabel.setVisible(true);
                    mainForm.coordsFireButton.setVisible(true);
                    mainForm.labelDescription.setText("<html>Для выбора позиции для удара,<br />нажмите дважды по нужной клетке на карте<br />Либо же введите координаты в поле ниже!</html>");
                    mainForm.frame.pack();

                    mainForm.playerUnderFire = player2;
                    mainForm.firingPlayer = player1;
                    mainForm.playGame();
                }
            }
        });
        mainForm.coordsFireButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainForm.coordsEdit.getDocument().getLength() >= 2 && mainForm.coordsEdit.getDocument().getLength() <= 4) {
                    String coords = mainForm.coordsEdit.getText().toLowerCase();
                    int x = -1;
                    int y = -1;

                    for (int i = 0; i < coords.length(); i++)
                        if (!allowedChars.contains(coords.charAt(i)))
                            return;

                    for (int i = numbers.length - 1; i >= 0; i--)
                        if (coords.contains(numbers[i])) {
                            if (y == -1)
                                y = i;
                            else
                                return;
                        }

                    for (int i = 0; i < letters.length; i++)
                        if (coords.contains(letters[i])) {
                            if (x == -1)
                                x = i;
                            else
                                return;
                        }

                    if (x != -1 && y != -1 && !Ship.isFiredBlock(mainForm.playerUnderFire.getField().getBlock(x, y)))
                        mainForm.fireByCoords(x + 1, y + 1);
                }
                mainForm.coordsFireButton.setEnabled(false);
            }
        });
        mainForm.frame.setContentPane(mainForm.contentPane);
        mainForm.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainForm.frame.pack();
        mainForm.frame.setLocationRelativeTo(null);
        mainForm.frame.setResizable(false);
        mainForm.frame.setVisible(true);
    }

    private void checkDataInCoordsEdit() {
        coordsFireButton.setEnabled(false);
        if (coordsEdit.getDocument().getLength() >= 2 && coordsEdit.getDocument().getLength() <= 4) {
            String coords = coordsEdit.getText().toLowerCase();
            int x = -1;
            int y = -1;

            for (int i = 0; i < coords.length(); i++)
                if (!allowedChars.contains(coords.charAt(i)))
                    return;

            for (int i = numbers.length - 1; i >= 0; i--)
                if (coords.contains(numbers[i])) {
                    if (y == -1)
                        y = i;
                    else if (y != 9 || i != 0)
                        return;
                }

            for (int i = 0; i < letters.length; i++)
                if (coords.contains(letters[i])) {
                    if (x == -1)
                        x = i;
                    else
                        return;
                }

            coordsFireButton.setEnabled((x != -1 && y != -1 && !Ship.isFiredBlock(playerUnderFire.getField().getBlock(x, y))));
        }
    }

    private void fireByCoords(final int x, final int y) {
        coordsEdit.setText("");
        if (!playerUnderFire.getField().fire(x - 1, y - 1))
            System.out.println("This block cannot be fired");
        else {
            JDialog dialog = new JDialog();
            JLabel label = new JLabel();

            JButton button = new JButton();
            button.setText("Дальше");
            button.addActionListener((e) -> {
                dialog.setVisible(false);
            });

            dialog.setTitle("Результат выстрела");
            dialog.setModal(true);
            dialog.setAlwaysOnTop(true);
            dialog.setLocationRelativeTo(null);
            dialog.setResizable(false);
            dialog.setSize(300, 100);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            dialog.add(label);
            dialog.add(button, BorderLayout.SOUTH);

            mapViewComponent.selectBlock(-1, -1);
            label.setText(Ship.isFiredShipBlock(playerUnderFire.getField().getBlock(x - 1, y - 1)) ? "Попадание!" : "Промах!");
            dialog.setVisible(true);

            if (playerUnderFire.isGameOver()) {
                JDialog gameOverDialog = new JDialog();
                JLabel gameOver = new JLabel("Игра окончена!\nПобедил игрок " + firingPlayer.getPlayerIndex());

                JButton gameOverButton = new JButton();

                gameOverDialog.setTitle("Конец игры");
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

                    pathEditPlayer1.setVisible(true);
                    pathEditPlayer2.setVisible(true);
                    pathLabelPlayer1.setVisible(true);
                    pathLabelPlayer2.setVisible(true);
                    playButton.setVisible(true);
                    mapViewComponent.setVisible(false);
                    mapLabelTitle.setVisible(false);
                    coordsEdit.setVisible(false);
                    coordsLabel.setVisible(false);
                    coordsFireButton.setVisible(false);
                    labelDescription.setText("Чтобы начать играть, введите пути до двух файлов с картами (.shf-файл)!");
                    frame.pack();
                });
                gameOverDialog.add(gameOverButton, BorderLayout.SOUTH);

                gameOverDialog.setVisible(true);
                return;
            }

            if (!Ship.isFiredShipBlock(playerUnderFire.getField().getBlock(x - 1, y - 1))) {
                Player buf = playerUnderFire;
                playerUnderFire = firingPlayer;
                firingPlayer = buf;
                mapLabelTitle.setText(firingPlayer.getPlayerIndex() == 1 ? "<html>Ход 1-го игрока<br />Поле противника:</html>" : "<html>Ход 2-го игрока<br />Поле противника:</html>");
                mapViewComponent.setField(playerUnderFire.getField());
                mapViewComponent.repaint();
            }
        }
    }

    private void playGame() {
        mapLabelTitle.setText("<html> Ход 1-го игрока<br />Поле противника:</html>");
        mapViewComponent.setField(playerUnderFire.getField());
        mapViewComponent.repaint();
        mapViewComponent.setFireListener(this::fireByCoords);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(8, 2, new Insets(10, 10, 10, 10), -1, -1));
        pathLabelPlayer1 = new JLabel();
        pathLabelPlayer1.setText("Путь до карты 1-го игрока");
        contentPane.add(pathLabelPlayer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(168, 17), null, 0, false));
        pathLabelPlayer2 = new JLabel();
        pathLabelPlayer2.setText("Путь до карты 2-го игрока");
        contentPane.add(pathLabelPlayer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(168, 17), null, 0, false));
        playButton = new JButton();
        playButton.setLabel("Играть");
        playButton.setText("Играть");
        contentPane.add(playButton, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapViewComponent = new MapViewComponent();
        mapViewComponent.setVisible(false);
        contentPane.add(mapViewComponent, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(330, 330), new Dimension(330, 330), new Dimension(330, 330), 0, false));
        pathEditPlayer1 = new JTextField();
        contentPane.add(pathEditPlayer1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pathEditPlayer2 = new JTextField();
        contentPane.add(pathEditPlayer2, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapLabelTitle = new JLabel();
        mapLabelTitle.setAlignmentX(0.5f);
        mapLabelTitle.setText("<html>Ход 1-го игрока<br />Поле противника:</html>");
        mapLabelTitle.setVisible(false);
        contentPane.add(mapLabelTitle, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelDescription.setAlignmentX(0.5f);
        labelDescription.setText("Чтобы начать играть, введите пути до двух файлов с картами (.shf-файл)!");
        contentPane.add(labelDescription, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        coordsEdit = new JTextField();
        coordsEdit.setVisible(false);
        contentPane.add(coordsEdit, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        coordsLabel = new JLabel();
        coordsLabel.setText("Координаты");
        coordsLabel.setVisible(false);
        contentPane.add(coordsLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        coordsFireButton = new JButton();
        coordsFireButton.setEnabled(false);
        coordsFireButton.setLabel("Выстрелить");
        coordsFireButton.setText("Выстрелить");
        coordsFireButton.setVisible(false);
        contentPane.add(coordsFireButton, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathLabelPlayer1.setLabelFor(pathEditPlayer1);
        pathLabelPlayer2.setLabelFor(pathEditPlayer2);
        coordsLabel.setLabelFor(coordsEdit);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        labelDescription = new JLabel("Some", JLabel.CENTER);
    }
}
