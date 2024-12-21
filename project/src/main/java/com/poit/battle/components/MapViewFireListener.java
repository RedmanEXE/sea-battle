package com.poit.battle.components;

/**
 * Интерфейс для создания слушателя для выстрелов по
 * карте в интерфейсных объектах классов, которые поддерживают
 * это
 */
public interface MapViewFireListener {
    /**
     * Вызывается, когда обнаружен выстрел по карте
     *
     * @param x Координата X клетки, где произошёл выстрел
     * @param y Координата Y клетки, где произошёл выстрел
     */
    void onFire(final int x, final int y);
}
