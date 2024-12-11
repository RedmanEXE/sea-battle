package com.poit.battle;

import com.poit.battle.models.Player;

import java.util.Scanner;

public class Main {

    static boolean isValidPosition(int x, int y) {return 0 <= x && x <= 9 && 0 <= y && y <= 9;}

    static void playGame(Player player1, Player player2) {
        Player playerUnderFire = player2;
        Player firingPlayer = player1;
        Scanner scanner = new Scanner(System.in);
        int x, y;
        //TODO : Поясняющие выводы
        do {
            firingPlayer.getField().printField();
            x = scanner.nextInt();
            y = scanner.nextInt();
            //TODO : Реализовать проверку корректности ввода
            if (playerUnderFire.getField().fire(x, y)) {
                System.out.println("Попадание!");
            }
            else
            {
                System.out.println("Промах!");
                playerUnderFire = (playerUnderFire.equals(player1) ? player2 : player1);
                firingPlayer = (firingPlayer.equals(player1) ? player2 : player1);

            }
        }while(!playerUnderFire.isGameOver());
        //TODO : Реализовать выбор победителя и вывод соответствующего сообщения
    }

    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}