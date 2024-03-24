package yiding.notepad.view;

import yiding.notepad.view.component.AbstractDialog;
import yiding.notepad.contol.FindAndReplaceDialogController;

import java.awt.*;

public class FindAndReplaceDialog extends AbstractDialog<FindAndReplaceDialogController> {

    public FindAndReplaceDialog(String title, Dimension size, Class clazz) {
        super(title, size, clazz);
    }

    @Override
    public void initDialogContent() {

    }
}
