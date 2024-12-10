package com.poit.battle.ui;

import java.util.HashMap;
import java.util.Map;

public class LanguageStorage {
    // TODO: Добавить поддержку разных языков (?)

    private static final Map<String, String> languageFile = new HashMap<>();

    static {
        languageFile.put("info.name", "Русский");
        languageFile.put("info.version", "1.0");
        languageFile.put("errors.mapLoad.general", "Карта, находящаяся в файле, является неверной!");
        languageFile.put("errors.mapLoad.invalidData", "В карте, которая находится в файле, найдены ошибки в данных!");
        languageFile.put("errors.mapLoad.fileNotAvailable", "Файл с картой недоступен!");
    }

    public static String getString(final String key) {
        return languageFile.get(key);
    }
}
