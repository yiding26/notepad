package yiding.text.view.window;

import yiding.text.view.component.AbstractDialog;
import yiding.text.view.service.FindAndReplaceDialogService;

import java.awt.*;

public class FindAndReplaceDialog extends AbstractDialog<FindAndReplaceDialogService> {

    public FindAndReplaceDialog(String title, Dimension size, Class clazz) {
        super(title, size, clazz);
    }

    @Override
    public void initDialogContent() {

    }
}
