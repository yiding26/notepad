package yiding.notepad.view.component;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    public Label(String string) {
        super(string);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }
}
