package yiding.notepad.view.component;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MenuItem extends JMenuItem {
    private static final int key;
    static {
        if ("Mac OS X".equals(System.getProperty("os.name"))) key = KeyEvent.META_DOWN_MASK;
        else key = KeyEvent.CTRL_DOWN_MASK;
    }

    public MenuItem(String text, ActionListener l) {
       this(text, l, -1);
    }

    public MenuItem(String text, ActionListener l, int keycode) {
        this(text, l, keycode, 0);
    }

    public MenuItem(String text, ActionListener l, int keycode, int modifiers) {
        super(text);
        if (keycode != -1)
            this.setAccelerator(KeyStroke.getKeyStroke(keycode, key + modifiers));
        //getRootPane().registerKeyboardAction(l, KeyStroke.getKeyStroke(keycode, key + modifiers), JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.addActionListener(l);
    }
}
