package yiding.notepad.view.window;

import yiding.notepad.view.component.AbstractDialog;
import yiding.notepad.view.component.Label;
import yiding.notepad.view.service.LoadingDialogService;

import java.awt.*;
import javax.swing.*;


public class LoadingDialog extends AbstractDialog<LoadingDialogService> {
    public LoadingDialog() {
        super("Loading", new Dimension(300, 200), LoadingDialogService.class);
        initDialogContent();
        setAlwaysOnTop(true);
    }

    @Override
    public void initDialogContent() {
        addComponent(BorderLayout.NORTH, "label", new Label(""));
        addComponent(BorderLayout.CENTER, "progressBar", new JProgressBar());
        pack();
    }

    public void setMinAndMax(int min, int max) {
        JProgressBar progressBar = getComponent("progressBar");
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
    }

    public void updateProgress(int progress, String text) {
        JProgressBar progressBar = getComponent("progressBar");
        progressBar.setValue(progress);
        SwingUtilities.invokeLater(progressBar::repaint);

        Label label = getComponent("label");
        label.setText(text);
    }
}
