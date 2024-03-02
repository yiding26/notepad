package yiding.notepad.view.service;

import yiding.Main;
import yiding.notepad.utils.FileIO;
import yiding.notepad.view.component.InsertButton;
import yiding.notepad.view.component.TextArea;
import yiding.notepad.view.window.NotepadWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;

public class NotepadWindowService extends AbstractWindowService {
    public boolean isSave = true;
    static final String defaultPath = Main.language.getLanguage("notepad.defaultFileName");
    public String last = "";
    public FileIO file = new FileIO(defaultPath);

    /**
     * 刷新窗口的标题
     */
    public void refreshWindow() {
        TextArea textArea = this.getComponent("textarea");
        if (textArea.isEditable()) {
            this.isSave = this.last.equals(textArea.getText());
            String title = "";
            if (!this.isSave) title = "* ";
            this.setTitle(title + new File(this.file.path).getName());
        } else this.setTitle(new File(this.file.path).getName());

        InsertButton tipButton = getComponent("tipButton");
        tipButton.setText(file.path);

        InsertButton caretPosButton = getComponent("caretPosButton");
        if (textArea.isEditable()) {
            Point pos = textArea.getCaretPos();
            String caretPosText = pos.x + ":" + pos.y;
            if (textArea.getSelectedText() != null) {
                caretPosText += " ";
                caretPosText += String.format(Main.language.getLanguage("notepad.caretPos.selected.format"), textArea.getSelectedText().length());
            }
            caretPosButton.setText(caretPosText);
        } else caretPosButton.setText("");
    }

    public void drop(DropTargetDropEvent dtde) {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);
            Transferable tf = dtde.getTransferable();
            try {
                java.util.List<File> files = (java.util.List<File>) tf.getTransferData(DataFlavor.javaFileListFlavor);
                dtde.dropComplete(true);
                for (File file : files) {
                    NotepadWindow notepadWindow = new NotepadWindow();
                    new Thread(() -> {
                        notepadWindow.showWindow();
                        notepadWindow.service.file.path = file.getAbsolutePath();
                        notepadWindow.service.loadFile();
                    }).start();
                }
                if (isSave) getWindow().closeWindow();
            } catch (UnsupportedFlavorException | IOException e) {
                Main.logger.error(e.getMessage());
            }
        } else dtde.rejectDrop();
    }

    /**
     * 新建文件
     */
    public void newFile() {
        if (!this.isSave) {
            int result = JOptionPane.showConfirmDialog(this.getWindow(), Main.language.getLanguage("notepad.notSave"), this.file.path, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                if (!this.saveFile()) return;
            } else if (result == JOptionPane.CANCEL_OPTION) return;
        }
        this.file.path = (String) JOptionPane.showInputDialog(getWindow(), "", Main.language.getLanguage("notepad.newFile"), JOptionPane.QUESTION_MESSAGE, null, null, Main.language.getLanguage("notepad.defaultFileName"));
        TextArea textArea = this.getComponent("textarea");
        textArea.setEditable(false);
        this.last = "";
        textArea.setText(this.last);
        this.refreshWindow();
        textArea.setEditable(true);
    }

    /**
     * 打开文件
     */
    public void openFile() {
        if (!this.isSave) {
            int result = JOptionPane.showConfirmDialog(getWindow(), Main.language.getLanguage("notepad.notSave"), this.file.path, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                if (!this.saveFile()) return;
            } else if (result == JOptionPane.CANCEL_OPTION) return;
        }
        String newPath = getFile(Main.language.getLanguage("notepad.openFile"), "");
        if (newPath.isEmpty()) return;
        this.file = new FileIO(newPath);
        this.loadFile();
        Main.logger.info(String.format("open file %s", file.path));
    }

    public boolean saveFile() {
        String oldPath = this.file.path;
        if (this.file.path.equals(defaultPath)) {
            this.file.path = this.getFile(Main.language.getLanguage("notepad.saveFile"), "");
            if (oldPath.equals(this.file.path) || this.file.path.isEmpty()) return false;
        }
        TextArea textArea = getComponent("textarea");
        if (this.file.write(textArea.getText())) {
            last = textArea.getText();
            Main.logger.info(String.format("save file %s", file.path));
        } else JOptionPane.showMessageDialog(getWindow(), Main.language.getLanguage("notepad.saveFailed"), this.file.path, JOptionPane.ERROR_MESSAGE);
        refreshWindow();
        return true;
    }

    /**
     * 另存为文件
     */
    public void saveAsFile() {
        String lastPath = this.file.path;
        this.file.path = this.getFile(Main.language.getLanguage("notepad.saveAsFile"), this.file.path);
        if (this.file.path.equals(lastPath) || this.file.path.isEmpty()) return;
        TextArea textArea = getComponent("textarea");
        if (file.write(textArea.getText())) last = textArea.getText();
        else JOptionPane.showMessageDialog(getWindow(), Main.language.getLanguage("notepad.saveFailed"), this.file.path, JOptionPane.ERROR_MESSAGE);
        refreshWindow();
    }

    /**
     * @param mode 模式 save或open作为窗口的标题以及按钮
     * @param path 默认路径
     * @return 文件路径
     */
    private String getFile(String mode, String path) {
        JFileChooser fileChooser = new JFileChooser();
        if (!path.isEmpty()) {
            fileChooser.setCurrentDirectory(new File(path));
            fileChooser.setSelectedFile(new File(path));
        }
        int option = fileChooser.showDialog(getWindow(), mode);
        if (option == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile().getAbsolutePath();
        else if (option == JFileChooser.CANCEL_OPTION)
            if (!path.isEmpty()) return path;
        return "";
    }

    public void loadFile() {
        TextArea textArea = getComponent("textarea");
        refreshWindow();
        this.last = this.file.read();
        isSave = true;
        new Thread(() -> {
            textArea.setText(this.last);
            refreshWindow();
            Main.logger.info(String.format("load file %s", file.path));
        }).start();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean onClose() {
        if (!this.isSave) {
            int result = JOptionPane.showConfirmDialog(getWindow(), Main.language.getLanguage("notepad.notSave"), this.file.path, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) saveFile();
            else return result != JOptionPane.CANCEL_OPTION;
        }
        return true;
    }
}
