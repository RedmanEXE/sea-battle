package com.poit.battle.components;

import com.poit.battle.models.Field;
import com.poit.battle.models.Ship;
import com.poit.battle.utils.TextDrawerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Элемент UI, отображающий карту из класса {@link Field},
 * позволяющий обрабатывать и передавать информацию о выстрелах,
 * которые хочет провести игрок
 */
@SuppressWarnings("unused")
public class MapViewComponent extends JComponent {
    private Field field = null;
    private int selectedBlockX = -1;
    private int selectedBlockY = -1;
    private MapViewFireListener mapViewFireListener;

    /**
     * Стандартный конструктор для элемента просмотрщика
     * карты
     */
    public MapViewComponent() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (field == null)
                    return;

                Field.Block block = field.getBlock(e.getX() / 30 - 1, e.getY() / 30 - 1);
                if (block == null || Ship.isFiredBlock(block)) {
                    selectedBlockX = -1;
                    selectedBlockY = -1;
                }

                if (selectedBlockX != e.getX() / 30 || selectedBlockY != e.getY() / 30) {
                    selectedBlockX = e.getX() / 30;
                    selectedBlockY = e.getY() / 30;
                    MapViewComponent.this.repaint();
                } else {
                    if (mapViewFireListener != null)
                        mapViewFireListener.onFire(selectedBlockX, selectedBlockY);
                    System.out.println("This click requires attention!");
                }
            }
        };
        this.addMouseListener(mouseAdapter);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (field != null) {
            for (int x = 0; x < 11; x++) {
                for (int y = 0; y < 11; y++) {
                    // Отрисовка карты
                    g.setColor(Color.BLACK);
                    g.drawRect(x * 30 + (x == 0 ? 1 : 0), y * 30 + (y == 0 ? 1 : 0), 30, 30);
                    if ((x + y) == 0)
                        g.setColor(new Color(234, 193, 152));
                    else if (x == 0 || y == 0)
                        g.setColor(new Color(200, 200, 200));
                    else
                        g.setColor(Color.WHITE);
                    g.fillRect(x * 30 + (x == 0 ? 2 : 1), y * 30 + (y == 0 ? 2 : 1), 28, 28);

                    if ((x + y) == 0)
                        continue;

                    // Отрисовка букв на карте
                    if (x == 0 || y == 0) {
                        g.setColor(new Color(100, 100, 100));
                        if (x == 0)
                            TextDrawerUtils.drawCenteredString(
                                    g,
                                    String.valueOf(y),
                                    new Rectangle(0, y * 30, 30, 30),
                                    new Font("Arial", Font.BOLD, 20)
                            );
                        else
                            TextDrawerUtils.drawCenteredString(
                                    g,
                                    String.valueOf((char) (x + 64)),
                                    new Rectangle(x * 30, 0, 30, 30),
                                    new Font("Arial", Font.BOLD, 20)
                            );
                    } else {
                        String f = "";
                        if (x == selectedBlockX && y == selectedBlockY) {
                            g.setColor(new Color(166, 37, 37));
                            f = "X";
                        } else {
                            Field.Block block = field.getBlock(x - 1, y - 1);
                            if (block == null)
                                continue;

                            switch (block) {
                                case FIRED -> {
                                    g.setColor(new Color(97, 74, 59));
                                    f = "Р";
                                }
                                case KILLED -> {
                                    g.setColor(new Color(211, 160, 160));
                                    f = "У";
                                }
                                case MISSED -> {
                                    g.setColor(new Color(160, 160, 160));
                                    f = ".";
                                }
                            }
                        }
                        TextDrawerUtils.drawCenteredString(g, f, new Rectangle(x * 30, y * 30, 30, 30), new Font("Arial", Font.BOLD, 20));
                    }
                }
            }
        }
    }

    /**
     * Устанавливает новое поле для отображения в этом элементе UI
     *
     * @param field Объект класса {@link Field}, содержащий карту
     *              для отображения. <br />
     *              Не может быть равен {@code null}
     */
    public final void setField(final @NotNull Field field) {
        this.field = field;
        this.selectedBlockX = -1;
        this.selectedBlockY = -1;
    }

    /**
     * Устанавливает новый слушатель для выбора места выстрела
     * игроком
     *
     * @param listener Интерфейс класса {@link MapViewFireListener}, который
     *                 будет вызываться при требовании выстрела. <br />
     *                 Может быть равен {@code null}
     */
    public final void setFireListener(final @Nullable MapViewFireListener listener) {
        this.mapViewFireListener = listener;
    }

    public final void selectBlock(final int x, final int y) {
        this.selectedBlockX = x;
        this.selectedBlockY = y;
        repaint();
    }
}
