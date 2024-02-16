package yiding.text.view.service;

import yiding.text.view.component.AbstractDialog;

import java.awt.*;
import java.util.HashMap;

public abstract class AbstractDialogService {

    private AbstractDialog<? extends AbstractDialogService> dialog;
    private HashMap<String, Component> componentMap;

    public final void setWindow(AbstractDialog<? extends AbstractDialogService> abstractWindow, HashMap<String, Component> componentMap) {
        this.dialog = abstractWindow;
        this.componentMap = componentMap;
    }

    @SuppressWarnings("unchecked")
    public final <T extends Component> T getComponent(String name){
        return (T) this.componentMap.get(name);
    }

    public final <T extends Component> T getParentWindowComponent(String name){
        return this.getDialog().parentWindow.getComponent(name);
    }

    public final AbstractDialog<? extends AbstractDialogService> getDialog(){
        return this.dialog;
    }
    public boolean onClose() {
        return true;
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public void setIconImage(Image iconImage) {
        dialog.setIconImage(iconImage);
    }
}
