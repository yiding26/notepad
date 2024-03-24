package yiding.notepad.contol;

import yiding.notepad.view.component.AbstractDialog;

import java.awt.*;
import java.util.HashMap;

public abstract class AbstractDialogController {

    private AbstractDialog<? extends AbstractDialogController> dialog;
    private HashMap<String, Component> componentMap;

    public final void setWindow(AbstractDialog<? extends AbstractDialogController> abstractWindow, HashMap<String, Component> componentMap) {
        this.dialog = abstractWindow;
        this.componentMap = componentMap;
    }

    @SuppressWarnings("unchecked")
    public final <T extends Component> T getComponent(String name){
        return (T) this.componentMap.get(name);
    }

    public final AbstractDialog<? extends AbstractDialogController> getDialog(){
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
