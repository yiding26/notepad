package yiding.notepad.view;


import yiding.notepad.contol.NotepadWindowController;
import yiding.notepad.utils.Language;
import yiding.notepad.view.component.Menu;
import yiding.notepad.view.component.MenuItem;
import yiding.notepad.view.component.ScrollPane;
import yiding.notepad.view.component.TextArea;
import yiding.notepad.view.component.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NotepadWindow extends AbstractWindow<NotepadWindowController> {
    public NotepadWindow() {
        super("", new Dimension(800, 600), NotepadWindowController.class);
        setCloseMode(AbstractWindow.DISPOSE_OR_EXIT);
        initWindowContent();
    }

    @Override
    public void initWindowContent() {
        setupMenuBar(new JMenuBar(), menuBar -> {
            menuBar.setBackground(Color.white);
            menuBar.setBorder(new LineBorder(Color.white, 1, false));
            Menu file = new Menu(Language.getLanguage("file"));
            file.addMenuItem(Language.getLanguage("file.newWindow"), e -> new NotepadWindow().showWindow(), KeyEvent.VK_N, KeyEvent.SHIFT_DOWN_MASK);
            file.addSeparator();
            file.addMenuItem(Language.getLanguage("file.newFile"), e -> controller.newFile(), KeyEvent.VK_N);
            file.addMenuItem(Language.getLanguage("file.openFile"), e -> controller.openFile(), KeyEvent.VK_O);
            file.addMenuItem(Language.getLanguage("file.saveFile"), e -> controller.saveFile(), KeyEvent.VK_S);
            file.addMenuItem(Language.getLanguage("file.saveAsFile"), e -> controller.saveAsFile(), KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK);
            menuBar.add(file);
        });
        add(new ScrollPane(), scrollPane -> scrollPane.add(register("textarea", new TextArea(), textArea -> {
            textArea.addCaretListener((e) -> controller.refreshWindow());
            textArea.setDropTarget(new DropTarget(this, DnDConstants.ACTION_REFERENCE, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent dtde) {
                    controller.drop(dtde);
                }
            }));
            register("popupmenu", new JPopupMenu(), popupMenu -> {
                popupMenu.add(new MenuItem(Language.getLanguage("textarea.undo"), e -> textArea.undo(), KeyEvent.VK_Z));
                popupMenu.add(new MenuItem(Language.getLanguage("textarea.redo"), e -> textArea.redo(), KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK));
                popupMenu.addSeparator();
                popupMenu.add(new MenuItem(Language.getLanguage("textarea.copy"), e -> textArea.copy(), KeyEvent.VK_C));
                popupMenu.add(new MenuItem(Language.getLanguage("textarea.cut"), e -> textArea.cut(), KeyEvent.VK_X));
                popupMenu.add(new MenuItem(Language.getLanguage("textarea.paste"), e -> textArea.paste(), KeyEvent.VK_V));
                popupMenu.addSeparator();
                popupMenu.add(new MenuItem(Language.getLanguage("textarea.gotoLAC"), e -> textArea.gotoLAC(), KeyEvent.VK_G));
                textArea.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger())
                            popupMenu.show(textArea, e.getX(), e.getY());
                    }
                });
            });
        })), BorderLayout.CENTER);
        add(Box.createHorizontalBox(), box -> {
            box.setBounds(0, getHeight() - 40, getWidth(), 20);
            box.setOpaque(false);
            box.add(register("tipButton", new InsertButton(), tipButton -> tipButton.addActionListener(e -> register("tipButton.popupmenu", new JPopupMenu(), popupMenu -> {
                popupMenu.add(new MenuItem(Language.getLanguage("CCOS.openInFileBrowser"), e1 -> controller.openInFileBrowser()));
                tipButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger())
                            popupMenu.show(tipButton, e.getX(), e.getY());
                    }
                });
            }))));
            box.add(Box.createHorizontalGlue());
            TextArea textArea = get("textarea");
            box.add(register("caretPosButton", new InsertButton(), insertButton -> insertButton.addActionListener(e -> textArea.gotoLAC())));
        }, BorderLayout.SOUTH);
        getContentPane().setBackground(Color.white);
    }
}
