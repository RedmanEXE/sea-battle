package com.poit.battle.models;

/**
 * Класс с основной логикой для игрока.
 * <p />
 * Является хранилищем для поля, а также позволяет проверять,
 * не проиграл ли данный игрой.
 */
public class Player {
    private final Field field;
    private final int playerIndex;

    /**
     * Стандартный конструктор для объекта игрока
     *
     * @param field Объект класса {@link Field}, привязанный к этому игроку
     */
    public Player(Field field, int playerIndex) {
        this.field = field;
        this.playerIndex = playerIndex;
    }

    /**
     * Метод для возвращения поля, привязанного к этому объекту
     *
     * @return Объект класса {@link Field}, привязанный к этому игроку
     */
    public Field getField() {
        return field;
    }

    /**
     * Метод для возвращения индекса игрока
     *
     * @return Индекс, привязанный к этому игроку
     */
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * Возвращает значение в зависимости от того, закончилась ли игра для
     * этого объекта игрока.
     * <p />
     * Определяется это путём проверки всего поля на наличие {@code Block.SHIP}
     * и если ни один из блоков не является кораблём, то считается, что игрок проиграл.<br />
     * Если же хоть один подобный блок найден, то функция вернёт {@code false}
     *
     * @return {@code true}, если игрок проиграл, в противном случае {@code false}
     */
    public boolean isGameOver() {
        for(int i = 0; i < 10 ; i++)
            for(int j = 0; j < 10; j++)
                if(field.getBlock(i, j) == Field.Block.SHIP)
                    return false;

        return true;
    }
}
