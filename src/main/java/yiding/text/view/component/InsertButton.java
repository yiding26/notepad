package yiding.text.view.component;

import javax.swing.*;
import java.awt.*;

public class InsertButton extends JButton {
    final Color defaultBackgoundColor;

    public InsertButton(String text) {
        super(text);
        setBorderPainted(false);
        setFocusPainted(false);
        setBackground(Color.white);
        defaultBackgoundColor = getBackground();
        addActionListener(e -> setFocusable(false));
    }

    public InsertButton() {
        this("");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
