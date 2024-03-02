package yiding.notepad.view.component;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Menu extends JMenu {
    public Menu(String name) {
        super(name);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    public void addMenuItem(String text, ActionListener l, int keycode, int modifiers) {
        add(new MenuItem(text, l, keycode, modifiers));
    }

    public void addMenuItem(String text, ActionListener l, int keycode) {
        add(new MenuItem(text, l, keycode));
    }
}
