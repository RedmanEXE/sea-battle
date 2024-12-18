package com.poit.battle.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Класс функций-утилит для работы с рисованием текста
 */
public class TextDrawerUtils {
    /**
     * Рисует на переданном холсте (в виде класса {@link Graphics})
     * текст, который будет отцентрирован так, как будто бы он
     * рисуется в определённой зоне по её центру.
     *
     * @param g Объект класса {@link Graphics} для отрисовки
     * @param text Строка с текстом, который необходимо вывести
     * @param rect Объект класса {@link Rectangle} для создания зоны
     *             отображения текста, в которой и будет отцентрирован
     *             текст
     * @param font Объект класса {@link Font}, содержащий информацию
     *             о шрифте, которым должен быть нарисован текст
     */
    public static void drawCenteredString(@NotNull Graphics g, String text, @NotNull Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setFont(font);
        g.drawString(text, x, y);
    }
}
