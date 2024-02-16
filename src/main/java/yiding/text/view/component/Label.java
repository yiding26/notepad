package yiding.text.view.component;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    public Label(Icon icon) {
        super(icon);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }

    public Label(String string) {
        super(string);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }
}
