package yiding.text.view.component;

import yiding.text.view.service.FileExplorerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FileExplorer extends AbstractDialog<FileExplorerService>{
    public FileExplorer() {
        super("", new Dimension(800, 800), FileExplorerService.class);
        initDialogContent();
        service.openFile(new File("."));
        showDialog();
    }

    @Override
    public void initDialogContent() {
        addComponent("scrollpnae", new ScrollPane(), scrollPane -> {
            addComponent(scrollPane, "filelist", new JList<File>(), fileList -> {
                fileList.setCellRenderer(new FileItemViewCellRender());
                fileList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        service.mouse(e);
                    }
                });
                addKeyboardAction(e -> service.enter(), KeyEvent.VK_ENTER);
            });
        });
    }
}
