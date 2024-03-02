package yiding.notepad.view.window;


import yiding.Main;
import yiding.notepad.view.component.AbstractWindow;
import yiding.notepad.view.component.Menu;
import yiding.notepad.view.component.MenuBar;
import yiding.notepad.view.component.MenuItem;
import yiding.notepad.view.component.PopupMenu;
import yiding.notepad.view.component.ScrollPane;
import yiding.notepad.view.component.TextArea;
import yiding.notepad.view.component.*;
import yiding.notepad.view.service.NotepadWindowService;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;

public class NotepadWindow extends AbstractWindow<NotepadWindowService> {
    public NotepadWindow() {
        super("", new Dimension(800, 600), NotepadWindowService.class);
        setCloseMode(AbstractWindow.DISPOSE_OR_EXIT);
        initWindowContent();
        initKeyboardAction();
    }

    @Override
    public void initWindowContent() {
        setupMenubar(new MenuBar(), menuBar -> {
            Menu file = new Menu(Main.language.getLanguage("menubar.file"));
            file.addMenuItem(Main.language.getLanguage("notepad.newNotepad"), e -> new NotepadWindow().showWindow(), KeyEvent.VK_N, KeyEvent.SHIFT_DOWN_MASK);
            file.addSeparator();
            file.addMenuItem(Main.language.getLanguage("notepad.newFile"), e -> service.newFile(), KeyEvent.VK_N);
            file.addMenuItem(Main.language.getLanguage("notepad.openFile"), e -> service.openFile(), KeyEvent.VK_O);
            file.addMenuItem(Main.language.getLanguage("notepad.saveFile"), e -> service.saveFile(), KeyEvent.VK_S);
            file.addMenuItem(Main.language.getLanguage("notepad.saveAsFile"), e -> service.saveAsFile(), KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK);
            menuBar.add(file);
        });
        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER, new ScrollPane(), scrollPane -> addComponentTo(scrollPane, "textarea", new TextArea(), textArea -> {
            textArea.addCaretListener((e) -> service.refreshWindow());
            textArea.setDropTarget(new DropTarget(this, DnDConstants.ACTION_REFERENCE, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent dtde) {
                    service.drop(dtde);
                }
            }));
            mapComponent("popupmenu", new PopupMenu(), popupMenu -> {
                popupMenu.add(new MenuItem(Main.language.getLanguage("textarea.popupmenu.undo"), e -> textArea.undo(), KeyEvent.VK_Z));
                popupMenu.add(new MenuItem(Main.language.getLanguage("textarea.popupmenu.redo"), e -> textArea.redo(), KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK));
                popupMenu.addSeparator();
                popupMenu.add(new MenuItem(Main.language.getLanguage("textarea.popupmenu.copy"), e -> textArea.copy(), KeyEvent.VK_C));
                popupMenu.add(new MenuItem(Main.language.getLanguage("textarea.popupmenu.cut"), e -> textArea.cut(), KeyEvent.VK_X));
                popupMenu.add(new MenuItem(Main.language.getLanguage("textarea.popupmenu.paste"), e -> textArea.paste(), KeyEvent.VK_V));
                popupMenu.addSeparator();
                popupMenu.add(new MenuItem(Main.language.getLanguage("textarea.popupmenu.goto"), e -> textArea.gotoLineAndColumn(), KeyEvent.VK_G));
                popupMenu.addTo(this);
            });
        }));
        addComponent(BorderLayout.SOUTH, Box.createHorizontalBox(), box -> {
            box.setBounds(0, getHeight() - 40, getWidth(), 20);
            box.setOpaque(false);
            addComponentTo(box, "tipButton", new InsertButton());
            box.add(Box.createHorizontalGlue());
            TextArea textArea = getComponent("textarea");
            addComponentTo(box, "caretPosButton", new InsertButton(), insertButton -> insertButton.addActionListener(e -> textArea.gotoLineAndColumn()));
        });
        getContentPane().setBackground(Color.white);
    }

    @Override
    public void initKeyboardAction() {
        TextArea textArea = getComponent("textarea");
        addKeyboardAction(e -> textArea.undo(), KeyEvent.VK_Z);
        addKeyboardAction(e -> textArea.redo(), KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK);
        addKeyboardAction(e -> textArea.copy(), KeyEvent.VK_C);
        addKeyboardAction(e -> textArea.cut(), KeyEvent.VK_X);
        addKeyboardAction(e -> textArea.paste(), KeyEvent.VK_V);
        addKeyboardAction(e -> textArea.gotoLineAndColumn(), KeyEvent.VK_G);
        addKeyboardAction(e -> service.newFile(), KeyEvent.VK_N);
        addKeyboardAction(e -> service.openFile(), KeyEvent.VK_O);
        addKeyboardAction(e -> service.saveFile(), KeyEvent.VK_S);
        addKeyboardAction(e -> service.saveAsFile(), KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK);
    }
}
