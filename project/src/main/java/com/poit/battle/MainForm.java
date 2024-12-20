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
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

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

    private Field player1Field = new Field(), player2Field = new Field();
    private Player playerUnderFire, firingPlayer;

    private String[] numbers = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};

    public static void main(String[] args) {
        MainForm mainForm = new MainForm();
        mainForm.frame = new JFrame("MainForm");
        mainForm.playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    File mapForPlayer1 = new File(mainForm.pathEditPlayer1.getText()), mapForPlayer2 = new File(mainForm.pathEditPlayer2.getText());
                    if (!DataChecker.isShipsFile(mainForm.pathEditPlayer1.getText()) ||
                            !DataChecker.checkFileAvailability(mainForm.pathEditPlayer1.getText(), true) ||
                            mainForm.player1Field.initFromFile(mapForPlayer1) != DataChecker.FieldCheckError.NONE)
                        return;

                    if (!DataChecker.isShipsFile(mainForm.pathEditPlayer2.getText()) ||
                            !DataChecker.checkFileAvailability(mainForm.pathEditPlayer2.getText(), true) ||
                            mainForm.player2Field.initFromFile(mapForPlayer2) != DataChecker.FieldCheckError.NONE)
                        return;

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
                            mainForm.coordsFireButton.setEnabled(e.getDocument().getLength() >= 2 && e.getDocument().getLength() <= 3);
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            mainForm.coordsFireButton.setEnabled(e.getDocument().getLength() >= 2 && e.getDocument().getLength() <= 3);
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            mainForm.coordsFireButton.setEnabled(e.getDocument().getLength() >= 2 && e.getDocument().getLength() <= 3);
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
        mainForm.frame.setContentPane(mainForm.contentPane);
        mainForm.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainForm.frame.pack();
        mainForm.frame.setLocationRelativeTo(null);
        mainForm.frame.setResizable(false);
        mainForm.frame.setVisible(true);
    }

    private void playGame() {
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

        mapLabelTitle.setText("<html> Ход 1-го игрока<br />Поле противника:</html>");
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
                    JLabel gameOver = new JLabel("Игра окончена!\nПобедил игрок " + firingPlayer.getPlayerIndex());

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
                    Player buf = playerUnderFire;
                    playerUnderFire = firingPlayer;
                    firingPlayer = buf;
                    mapLabelTitle.setText(firingPlayer.getPlayerIndex() == 1 ? "<html>Ход 1-го игрока<br />Поле противника:</html>" : "<html>Ход 2-го игрока<br />Поле противника:</html>");
                    mapViewComponent.setField(playerUnderFire.getField());
                    mapViewComponent.repaint();
                }
            }
        });
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
        this.$$$loadLabelText$$$(pathLabelPlayer1, this.$$$getMessageFromBundle$$$("values/strings", "form.pathEditPlayer1"));
        contentPane.add(pathLabelPlayer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(168, 17), null, 0, false));
        pathLabelPlayer2 = new JLabel();
        this.$$$loadLabelText$$$(pathLabelPlayer2, this.$$$getMessageFromBundle$$$("values/strings", "form.pathEditPlayer2"));
        contentPane.add(pathLabelPlayer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(168, 17), null, 0, false));
        playButton = new JButton();
        playButton.setLabel("Play");
        playButton.setText("Play");
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

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
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
