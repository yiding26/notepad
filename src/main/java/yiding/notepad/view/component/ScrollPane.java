package yiding.notepad.view.component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScrollPane extends JScrollPane {
    public ScrollPane() {
        setBackground(new Color(0, 0, 0, 0));
        this.setBorder(null);
        getHorizontalScrollBar().setUI(new ScrollBarUI());
        getVerticalScrollBar().setUI(new ScrollBarUI());
    }

    public Component add(Component component){
        this.setViewportView(component);
        return component;
    }

    static class ScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            LookAndFeel.installColors(scrollbar, "ScrollBar.background",
                    "ScrollBar.foreground");
            thumbColor = Color.gray;
            trackColor = Color.white;
            scrollbar.setBackground(new Color(0, 0, 0, 0));
            scrollbar.setOpaque(true);
            scrollbar.addMouseListener(new MouseAdapter() {
                boolean isMousePressed;
                @Override
                public void mousePressed(MouseEvent e) {
                    isMousePressed = true;
                    thumbColor = Color.darkGray;
                    scrollbar.repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isMousePressed = false;
                    thumbColor = Color.gray;
                    scrollbar.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isMousePressed) {
                        thumbColor = Color.gray;
                        scrollbar.repaint();
                    }
                }
            });
            scrollbar.setBackground(new Color(0, 0, 0, 0));
            scrollbar.setBorder(new LineBorder(Color.BLACK, 0, true));
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            c.setPreferredSize(new Dimension(12, 12));
            return super.getPreferredSize(c);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            InsertButton button = new InsertButton();
            button.setOpaque(false);
            button.setBackground(new Color(0, 0, 0, 0));
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            InsertButton button = new InsertButton();
            button.setOpaque(false);
            button.setBackground(new Color(0, 0, 0, 0));
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            int w = thumbBounds.width;
            int h = thumbBounds.height;
            g.translate(thumbBounds.x, thumbBounds.y);
            g.setColor(thumbColor);
            int i = 2;
            int arc;
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                arc = w / 2;
                if (h < 10) arc = 1;
            } else arc = h / 2;
            g.fillRoundRect(i, i, w - (2 * i), h - (2 * i), arc, arc);
            g.translate(-thumbBounds.x, -thumbBounds.y);
        }
    }
}
