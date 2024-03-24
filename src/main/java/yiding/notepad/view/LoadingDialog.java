package yiding.notepad.view;

import yiding.notepad.view.component.AbstractDialog;
import yiding.notepad.view.component.Label;
import yiding.notepad.contol.AbstractDialogController;

import java.awt.*;
import javax.swing.*;


public class LoadingDialog extends AbstractDialog<AbstractDialogController> {
    boolean isCanceled = false;
    public LoadingDialog() {
        super("Loading", new Dimension(300, 100), AbstractDialogController.class);
        initDialogContent();
        setAlwaysOnTop(true);
    }

    @Override
    public void initDialogContent() {
        setCloseMode(DO_NOTHING);
        add(register("label", new Label("")), BorderLayout.NORTH);
        add(register("progressBar", new JProgressBar()), BorderLayout.CENTER);
        add(register("button", new JButton("Cancel"), button -> button.addActionListener(e -> {
            isCanceled = true;
            button.setText("Cancelling");
            button.setEnabled(false);
        })), BorderLayout.SOUTH);
    }

    public void setMinAndMax(int min, int max) {
        JProgressBar progressBar = get("progressBar");
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
    }

    public void updateProgress(int progress, String text) {
        JProgressBar progressBar = get("progressBar");
        progressBar.setValue(progress);
        SwingUtilities.invokeLater(progressBar::repaint);
        if (!isCanceled) {
            Label label = get("label");
            label.setText(text);
        }
    }

    public boolean isCanceled() {
        return isCanceled;
    }
}
