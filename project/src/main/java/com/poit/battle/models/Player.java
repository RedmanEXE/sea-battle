package com.poit.battle.models;

public class Player {
    private Field field;

    public Player(Field field) {
        this.field = field;
    }
    public Field getField() {return field;}
    public boolean isGameOver(){
        boolean gameOver = true;
        for(int i = 0; i < 10 ; i++)
            for(int j = 0; j < 10; j++)
                if(field.getBlock(i, j) == Field.Block.SHIP) {
                    gameOver = false;
                    break;
                }
        return gameOver;
    }
}
