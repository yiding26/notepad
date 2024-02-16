package yiding;

import yiding.log.ConsoleOutput;
import yiding.log.LogManager;
import yiding.log.Logger;
import yiding.text.utils.Language;
import yiding.text.view.window.NotepadWindow;

import javax.swing.*;

public class Main {
    static final String version = "0.1.0";
    static final String NOTEPAD = "notepad";
    public static Logger logger = new Logger(NOTEPAD);
    public static Language language;
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        LogManager.add(new ConsoleOutput("[%level] [%c:%method] %m%n"));
        logger.info(NOTEPAD);
        logger.info(version);
        language = new Language();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        int len;
        if (args.length == 0) len = 1;
        else len = args.length;
        for (int i = 0; i < len; i++) {
            NotepadWindow notepadWindow = new NotepadWindow();
            notepadWindow.showWindow();
            if (args.length != 0) {
                notepadWindow.service.file.path = args[i];
                new Thread(() -> notepadWindow.service.loadFile()).start();
            }
        }
    }
}