package yiding.notepad.utils;

import yiding.NotepadApplication;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Language {
    static String name;
    static HashMap<String, String> language = new HashMap<>();
    public static void setupLanguage() {
        Language language = new Language();
        language.initLanguageName();
        language.findLanguageFile();
    }

    public void initLanguageName() {
        Locale locale = Locale.getDefault();
        name = locale.getLanguage() + "_" + locale.getCountry();
        NotepadApplication.logger.info(name);
    }

    public void findLanguageFile() {
        try (InputStream inputStream = Objects.requireNonNull(this.getClass().getClassLoader().getResource("yiding/notepad/lang/zh_cn.lang")).openStream()) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null)
                    if (!line.startsWith("#")) {
                        String[] inputs = line.split(":", 2);
                        if (inputs.length == 2)
                            language.put(inputs[0], inputs[1]);
                    }
            } else {
                JOptionPane.showMessageDialog(null, "Can not find language file.", "Error", JOptionPane.ERROR_MESSAGE);
                NotepadApplication.logger.error("Can not find language file.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLanguage(String name) {
        if (name.startsWith("CCOS")) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) name = name.replace("CCOS", "win");
            else if (os.contains("mac")) name = name.replace("CCOS", "mac");
            else if (os.contains("nux")) name = name.replace("CCOS", "nux");
            else name = name.replace("CCOS", "unk");
        }
        String s = language.get(name);
        if (s == null) return name;
        else return s;
    }
}