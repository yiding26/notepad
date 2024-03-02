package yiding.notepad.view.component;

import yiding.Main;
import yiding.notepad.view.service.AbstractDialogService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class AbstractDialog<R extends AbstractDialogService> extends JDialog {
    public static final int HIDE = 0, DISPOSE = 1, EXIT = 2;
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
        if (dialogCNT > 256) {
            int result = JOptionPane.showConfirmDialog(null, "the windows num is out of the limit.(" + dialogCNT + " > 256).\nDo you want to Open Another Dialog?", "Java Swing Dialog", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.NO_OPTION)
                this.dispose();
        }
        synchronized (this) {
            dialogCNT++;
        }
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
        try {
            this.service = clazz.getConstructor().newInstance();
            this.service.setWindow(this, this.componentMap);
        } catch (ReflectiveOperationException e) {
            Main.logger.error(e.getMessage());
        }
    }

    public void setParentWindow(JComponent parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void showDialog() {
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        super.setVisible(true);
    }

    public void closeDialog() {
        switch (closeMode) {
            case HIDE -> super.setVisible(false);
            case DISPOSE -> this.dispose();
            case EXIT -> System.exit(0);
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

    public <T extends Component> void addComponent(String name, T component){
        this.componentMap.put(name, component);
        this.add(component);
    }

    public <T extends Component> void addComponent(String name, T component, Consumer<T> consumer){
        if(consumer != null)
            consumer.accept(component);
        this.componentMap.put(name, component);
        this.add(component);
    }

    public <T extends Component> void addComponent(Object o, String name, T component){
        this.componentMap.put(name, component);
        this.add(component, o);
    }

    public <T extends Component> void addComponent(Container container, String name, T component, Consumer<T> consumer){
        if(consumer != null)
            consumer.accept(component);
        this.componentMap.put(name, component);
        container.add(component);
    }

    public <T extends Component> void mapComponent(String name, T component, Consumer<T> consumer){
        if(consumer != null)
            consumer.accept(component);
        this.componentMap.put(name, component);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(String name){
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
