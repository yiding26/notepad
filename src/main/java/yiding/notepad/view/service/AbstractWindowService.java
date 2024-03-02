package yiding.notepad.view.service;

import yiding.notepad.view.component.AbstractWindow;

import java.awt.*;
import java.util.HashMap;

public abstract class AbstractWindowService {

    private AbstractWindow<? extends AbstractWindowService> window;
    private HashMap<String, Component> componentMap;

    public final void setWindow(AbstractWindow<? extends AbstractWindowService> AbstractWindow, HashMap<String, Component> componentMap) {
        this.window = AbstractWindow;
        this.componentMap = componentMap;
    }

    @SuppressWarnings("unchecked")
    public final <T extends Component> T getComponent(String name){
        return (T) this.componentMap.get(name);
    }

    public final AbstractWindow<? extends AbstractWindowService> getWindow(){
        return this.window;
    }

    /**
     * 在按下窗口关闭按钮后执行的操作
     * @return 是否关闭窗口
     */
    public boolean onClose() {
        return true;
    }

    public void setTitle(String title) {
        window.setTitle(title);
    }

    public void setIconImage(Image iconImage) {
        window.setIconImage(iconImage);
    }
}
