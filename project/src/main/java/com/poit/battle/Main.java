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
        do {
            System.out.println((firingPlayer.equals(player1) ? "ХОД 1-ГО ИГРОКА" : "Ход 2-ГО ИГРОКА"));
            firingPlayer.getField().printField();
            System.out.println();
            System.out.println((firingPlayer.equals(player1) ? "1-Й ИГРОК ВВЕДИТЕ КООРДИНАТЫ:" : "2-Й ИГРОК ВВЕДИТЕ КООРДИНАТЫ:"));
            x = scanner.nextInt();
            y = scanner.nextInt();
            //TODO : Реализовать проверку корректности ввода
            if (playerUnderFire.getField().fire(x, y)) {
                System.out.println("ПОПАДАНИЕ!");
            }
            else
            {
                System.out.println("ПРОМАХ!");
                playerUnderFire = (playerUnderFire.equals(player1) ? player2 : player1);
                firingPlayer = (firingPlayer.equals(player1) ? player2 : player1);
            }
        }while(!playerUnderFire.isGameOver());
        scanner.close();
        System.out.println((firingPlayer.equals(player1) ? "ПОБЕДА ИГРОКА НОМЕР 1!" : "ПОБЕДА ИГРОКА НОМЕР 2!"));
    }

    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}