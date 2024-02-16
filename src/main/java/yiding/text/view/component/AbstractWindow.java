package yiding.text.view.component;

import yiding.Main;
import yiding.text.view.service.AbstractWindowService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class AbstractWindow<R extends AbstractWindowService> extends JFrame {
    public static final int HIDE = 0, DISPOSE = 1, EXIT = 2, DISPOSE_OR_EXIT = 3;
    int closeMode;
    public static int key, windowCNT;
    private final HashMap<String, Component> componentMap = new HashMap<>();
    public R service;

    static {
        if ("Mac OS X".equals(System.getProperty("os.name"))) key = KeyEvent.META_DOWN_MASK;
        else key = KeyEvent.CTRL_DOWN_MASK;
    }

    public AbstractWindow(String title, Dimension size, Class<R> clazz) {
        if (windowCNT > 64) {
            int result = JOptionPane.showConfirmDialog(null, "the windows num is out of the limit.(" + windowCNT + " > 256).\nDo you want to Open Another Window?", "Java Swing Window", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.NO_OPTION) {
                this.dispose();
            }
        }
        synchronized (this) {
            windowCNT++;
        }
        this.closeMode = DISPOSE;
        this.setTitle(title);
        this.setSize(size);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (service.onClose())
                    AbstractWindow.this.closeWindow();
            }
        });
        try {
            this.service = clazz.getConstructor().newInstance();
            this.service.setWindow(this, this.componentMap);
        } catch (ReflectiveOperationException e) {
            Main.logger.info(e.getMessage());
        }
    }

    public void showWindow() {
        super.setVisible(true);
    }

    public void closeWindow() {
        switch (closeMode) {
            case HIDE -> super.setVisible(false);
            case DISPOSE -> this.dispose();
            case EXIT -> System.exit(0);
            case DISPOSE_OR_EXIT -> {
                if (onlyOneWindow()) System.exit(0);
                else this.dispose();
            }
            default -> throw new RuntimeException("What does it mean? " + closeMode);
        }
    }

    @Override
    public void dispose() {
        synchronized (this) {
            windowCNT--;
        }
        super.dispose();
    }

    @Override
    public void setVisible(boolean b) {
        String method;
        if (b) method = "showWindow()";
        else method = "closeWindow()";
        throw new UnsupportedOperationException("禁止使用setVisible()，请使用" + method);
    }

    public <T extends Component> void addComponent(Object o, T component, Consumer<T> consumer){
        if(consumer != null)
            consumer.accept(component);
        this.add(component, o);
    }

    public <T extends Component> void addComponent(Object o, String name, T component, Consumer<T> consumer){
        if(consumer != null)
            consumer.accept(component);
        this.componentMap.put(name, component);
        this.add(component, o);
    }

    public  <T extends Component> void addComponentTo(Container target, String name, T component){
        addComponentTo(target, name, component, null);
    }

    public  <T extends Component> void addComponentTo(Container target, String name, T component, Consumer<T> consumer){
        if(consumer != null)
            consumer.accept(component);
        this.componentMap.put(name, component);
        target.add(component);
    }

    public <T extends JMenuBar> void setupMenubar(T menuBar, Consumer<T> consumer) {
        if(consumer != null)
            consumer.accept(menuBar);
        setJMenuBar(menuBar);
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

    public abstract void initWindowContent();
    public abstract void initKeyboardAction();

    public static boolean onlyOneWindow() {
        return windowCNT == 1;
    }

    public void addKeyboardAction(ActionListener anAction, int keyCode) {
        addKeyboardAction(anAction, keyCode, 0);
    }

    public void addKeyboardAction(ActionListener anAction, int keyCode, int modifiers) {
        getRootPane().registerKeyboardAction(anAction, KeyStroke.getKeyStroke(keyCode, key + modifiers), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
