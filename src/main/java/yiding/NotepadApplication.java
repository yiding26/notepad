package yiding;

import yiding.log.ConsoleOutput;
import yiding.log.LogManager;
import yiding.log.Logger;
import yiding.notepad.utils.Language;
import yiding.notepad.view.NotepadWindow;

import javax.swing.*;

public class NotepadApplication {
    static final String version = "0.1.0";
    static final String NOTEPAD = "notepad";
    public static Logger logger = new Logger(NOTEPAD);
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        LogManager.add(new ConsoleOutput("%d{HH:mm:ss.SSS} [%level] [%c:%method] %m%n"));
        logger.info(NOTEPAD);
        logger.info(version);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Language.setupLanguage();

        int len;
        if (args.length == 0) len = 1;
        else len = args.length;
        for (int i = 0; i < len; i++) {
            NotepadWindow notepadWindow = new NotepadWindow();
            notepadWindow.showWindow();
            if (args.length != 0) {
                notepadWindow.controller.file.path = args[i];
                new Thread(() -> notepadWindow.controller.loadFile()).start();
            }
        }
    }
}