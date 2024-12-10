package com.poit.battle.models;

/**
 * Ошибка, которая вызывается, если из объекта класса {@link Field} была
 * вызвана функция до полной инициализации
 */
public class FieldNotInitializedException extends RuntimeException {
    public FieldNotInitializedException(String message) {
        super(message);
    }
}
