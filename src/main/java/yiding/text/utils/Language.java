package yiding.text.utils;

import yiding.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class Language {
    String name;
    HashMap<String, String> language = new HashMap<>();
    public Language() {
        initLanguageName();
        findLanguageFile();
    }

    public void initLanguageName() {
        Locale locale = Locale.getDefault();
        name = locale.toString().toLowerCase(locale);
        Main.logger.info(name);
    }

    public void findLanguageFile() {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yiding/lang/" + name +".lang")) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                String[] strings = stringBuilder.toString().split("\n");
                for (String string : strings) {
                    String[] inputs = string.split(":", 2);
                    if (inputs.length == 2) {
                        language.put(inputs[0], inputs[1]);
                    }
                }
            } else {
                Main.logger.error("Can not find file.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLanguage(String name) {
        String s = language.get(name);
        if (s == null) return name;
        else return s;
    }
}