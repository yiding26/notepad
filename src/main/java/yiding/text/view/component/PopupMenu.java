package yiding.text.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupMenu extends JPopupMenu {
    public PopupMenu() {
        super();
    }

    public void addTo(Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    PopupMenu.this.show(component, e.getX(), e.getY());
            }
        });
    }
}
