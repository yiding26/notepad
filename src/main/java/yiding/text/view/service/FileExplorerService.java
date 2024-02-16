package yiding.text.view.service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class FileExplorerService extends AbstractDialogService {
    public void mouse(MouseEvent m) {
        JList fileListItemList = getComponent("filelist");
        if (fileListItemList.getSelectedIndex() != -1) {
            if (m.getClickCount() == 2) {
                File fileListItem = (File) fileListItemList.getSelectedValue();
                openFile(fileListItem);
            }
        }
    }

    public void enter() {
        JList fileListItemList = getComponent("filelist");
        if (fileListItemList.getSelectedIndex() != -1) {

        }
    }

    public void openFile(File file) {
        if (file.isDirectory()) {
            if (file.getAbsolutePath().endsWith(File.separator + "..")) {
                file = new File(file.getAbsolutePath().replace(File.separator + "..", "")).getParentFile();
            }
            if (file.getAbsolutePath().endsWith(File.separator + "."))
                file = new File(file.getAbsolutePath().replace(File.separator + ".", ""));
            Vector<File> fileVector = new Vector<>();
            String[] files = file.list();
            fileVector.add(new File(file.getAbsolutePath() + File.separator + ".."));
            if (files != null) {
                for (String s : files)
                    if (new File(s).isDirectory())
                        fileVector.add(new File(file.getAbsolutePath() + File.separator + s));
            }
            JList fileList = getComponent("filelist");
            setTitle(file.getAbsolutePath());
            fileList.setListData(fileVector);
        } else {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(getDialog(), e.getMessage(), file.getAbsolutePath(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
