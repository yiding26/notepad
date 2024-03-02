package yiding.notepad.view.component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MenuBar extends JMenuBar {
    public MenuBar() {
        super();
        setBackground(Color.white);
        setBorder(new LineBorder(Color.white, 1, false));
    }
}
