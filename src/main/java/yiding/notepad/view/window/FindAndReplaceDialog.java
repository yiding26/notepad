package yiding.notepad.view.window;

import yiding.notepad.view.component.AbstractDialog;
import yiding.notepad.view.service.FindAndReplaceDialogService;

import java.awt.*;

public class FindAndReplaceDialog extends AbstractDialog<FindAndReplaceDialogService> {

    public FindAndReplaceDialog(String title, Dimension size, Class clazz) {
        super(title, size, clazz);
    }

    @Override
    public void initDialogContent() {

    }
}
