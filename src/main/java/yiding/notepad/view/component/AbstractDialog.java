package yiding.notepad.view.component;

import yiding.NotepadApplication;
import yiding.notepad.contol.AbstractDialogController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class AbstractDialog<R extends AbstractDialogController> extends JDialog {
    public static final int DO_NOTHING = -1, HIDE = 0, DISPOSE = 1;
    static int key, dialogCNT;
    int closeMode;
    private final HashMap<String, Component> componentMap = new HashMap<>();
    public R service;
    public Component parentWindow;

    static {
        if ("Mac OS X".equals(System.getProperty("os.name"))) key = KeyEvent.META_DOWN_MASK;
        else key = KeyEvent.CTRL_DOWN_MASK;
    }

    public AbstractDialog(String title, Dimension size, Class<R> clazz) {
        super();
        this.closeMode = DISPOSE;
        this.setTitle(title);
        this.setSize(size);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (AbstractDialog.this.service.onClose())
                    AbstractDialog.this.closeDialog();
            }
        });
        if (clazz != null) {
            try {
                if (clazz != AbstractDialogController.class) {
                    this.service = clazz.getConstructor().newInstance();
                    this.service.setWindow(this, this.componentMap);
                }
            } catch (ReflectiveOperationException e) {
                NotepadApplication.logger.error(e.getMessage());
            }
        }
    }

    public AbstractDialog(String title, Dimension size) {
        this(title, size, null);
    }

    public void setParentWindow(JComponent parentWindow) {
        this.parentWindow = parentWindow;
        this.setLocationRelativeTo(parentWindow);
    }

    public void showDialog() {
        setLocationRelativeTo(parentWindow);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        super.setVisible(true);
    }

    public void closeDialog() {
        switch (closeMode) {
            case DO_NOTHING -> {/*do nothing*/}
            case HIDE -> super.setVisible(false);
            case DISPOSE -> this.dispose();
            default -> throw new RuntimeException("What does it mean? " + closeMode);
        }
    }

    @Override
    public void dispose() {
        synchronized (this) {
            dialogCNT--;
        }
        super.dispose();
    }

    @Override
    public void setVisible(boolean b) {
        String method;
        if (b) method = "showDialog()";
        else method = "closeDialog()";
        throw new UnsupportedOperationException("禁止使用setVisible()，请使用" + method);
    }

    public <T extends Component> T register(String name, T component){
        return register(name, component, null);
    }

    public <T extends Component> T register(String name, T component, Consumer<T> consumer){
        if (consumer != null)
            consumer.accept(component);
        this.componentMap.put(name, component);
        return component;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T get(String name){
        return  (T) this.componentMap.get(name);
    }

    public void setCloseMode(int closeMode) {
        this.closeMode = closeMode;
    }

    public abstract void initDialogContent();

    public void addKeyboardAction(ActionListener anAction, int keyCode) {
        getRootPane().registerKeyboardAction(anAction, KeyStroke.getKeyStroke(keyCode, key), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
