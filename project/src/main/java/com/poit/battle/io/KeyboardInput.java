package com.poit.battle.io;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.File;
import java.util.Scanner;

@SuppressWarnings("unused")
public class KeyboardInput implements Closeable {
    private final Scanner scanner;

    public KeyboardInput() {
        this.scanner = new Scanner(System.in);
    }

    public int takeInteger(final String text) {
        boolean isInCorrect;
        int value;
        isInCorrect = true;
        value = 0;
        while (isInCorrect) {
            System.out.print(text);
            try {
                value = Integer.parseInt(this.scanner.nextLine());
                isInCorrect = false;
            } catch (NumberFormatException e) {
                System.out.println("Введите число, а не строку или что-то иное!");
            }
        }

        return value;
    }

    public int takeIntegerInRange(final String text, final int min, final int max) {
        boolean isInCorrect;
        int value;
        isInCorrect = true;
        value = 0;
        while (isInCorrect) {
            value = this.takeInteger(text);
            isInCorrect = false;
            if ((value < min || value > max)) {
                isInCorrect = true;
                System.out.println("Значение должно находится в границах от " + min + " до " + max + "!");
            }
        }

        return value;
    }

    public String takeString(final String text) {
        System.out.print(text);
        return this.scanner.nextLine();
    }

    public String takeStringWithRequiredLength(final String text, final int minLength, final int maxLength) {
        boolean isInCorrect;
        String value;
        isInCorrect = true;
        value = "";
        while (isInCorrect) {
            value = this.takeString(text);
            isInCorrect = false;
            if ((value.length() < minLength || value.length() > maxLength)) {
                isInCorrect = true;
                System.out.println("Строка должна быть длиной от " + minLength + " до " + maxLength + "!");
            }
        }

        return value;
    }

    public File takeFile(final String text, final boolean read) {
        return this.takeFile(text, read, null);
    }

    public File takeFile(final String text, final boolean read, @Nullable final FilepathFilter filter) {
        boolean isInCorrect;
        String path = "";
        isInCorrect = true;
        while (isInCorrect) {
            path = this.takeString(text);
            isInCorrect = false;
            if (filter != null && !filter.accept(path)) {
                isInCorrect = true;
                System.out.println("Этот файл недоступен! Введите путь до другого файла!");
            }
        }

        return new File(path);
    }

    public void holdInput() {
        this.scanner.nextLine();
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
