package yiding.notepad.view.component;

import yiding.NotepadApplication;
import yiding.notepad.contol.AbstractWindowService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class AbstractWindow<R extends AbstractWindowService> extends javax.swing.JFrame {
    public static final int HIDE = 0, DISPOSE = 1, EXIT = 2, DISPOSE_OR_EXIT = 3;
    int closeMode;
    public static int key, windowCNT = 0;
    private final HashMap<String, Component> componentMap = new HashMap<>();
    public R controller;

    static {
        if ("Mac OS X".equals(System.getProperty("os.name"))) {
            key = KeyEvent.META_DOWN_MASK;
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } else key = KeyEvent.CTRL_DOWN_MASK;
    }

    public AbstractWindow(String title, Dimension size, Class<R> clazz) {
        synchronized (this) {
            windowCNT++;
        }
        this.closeMode = DISPOSE;
        this.setTitle(title);
        this.setSize(size);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (controller.onClose())
                    AbstractWindow.this.closeWindow();
            }
        });
        try {
            this.controller = clazz.getConstructor().newInstance();
            this.controller.setWindow(this, this.componentMap);
        } catch (ReflectiveOperationException e) {
            NotepadApplication.logger.info(e.getMessage());
        }
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });
    }

    public void showWindow() {
        super.setVisible(true);
    }

    public void closeWindow() {
        switch (closeMode) {
            case HIDE -> super.setVisible(false);
            case DISPOSE -> this.dispose();
            case EXIT -> {
                dispose();
                System.exit(0);
            }
            case DISPOSE_OR_EXIT -> {
                this.dispose();
                if (onlyOneWindow()) {
                    System.out.println(windowCNT);
                    System.exit(0);
                }
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

    public <T extends JMenuBar> void setupMenuBar(T menuBar, Consumer<T> consumer) {
        if(consumer != null)
            consumer.accept(menuBar);
        setJMenuBar(menuBar);
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

    public <T extends Component> T add(T component, Consumer<T> consumer) {
        return add(component, consumer, null);
    }

    public <T extends Component> T add(T component, Consumer<T> consumer, Object o) {
        if (consumer != null)
            consumer.accept(component);
        add(component, o);
        return component;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T get(String name){
        return  (T) this.componentMap.get(name);
    }

    public void setCloseMode(int closeMode) {
        this.closeMode = closeMode;
    }

    public abstract void initWindowContent();

    public static boolean onlyOneWindow() {
        return windowCNT == 0;
    }

    public void addKeyboardAction(ActionListener anAction, int keyCode) {
        addKeyboardAction(anAction, keyCode, 0);
    }

    public void addKeyboardAction(ActionListener anAction, int keycode, int modifiers) {
        getRootPane().registerKeyboardAction(anAction, KeyStroke.getKeyStroke(keycode, key + modifiers), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void resize() {}
}
