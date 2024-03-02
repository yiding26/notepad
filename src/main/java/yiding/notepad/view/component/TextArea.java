package yiding.notepad.view.component;

import yiding.Main;
import yiding.notepad.view.window.LoadingDialog;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class TextArea extends JTextArea {
    UndoManager undoManager = new UndoManager();
    public TextArea() {
        super();
        this.setUndoManager(undoManager);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
    }

    public Point getCaretPos() {
        int pos = this.getCaretPosition();
        int line, column;
        try {
            line = this.getLineOfOffset(pos);
            column = pos - this.getLineStartOffset(line);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        return new Point(line + 1, column + 1);
    }

    public void setCaretPos(Point point) {
        int line = point.x - 1, column = point.y - 1, index = 0;
        try {
            final int maxLine = this.getLineCount();
            if (line > maxLine)
                line = maxLine;
            final int maxColumn = this.getLineEndOffset(line) - this.getLineStartOffset(line);
            if (column > maxColumn)
                column = maxColumn;
            index += this.getLineStartOffset(line) + column;
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        this.setCaretPosition(index);
    }

    @Override
    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);
        if(transferable == null) return;
        if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String str = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                if (str.length() > 20 * 1024 * 1024) {
                    JOptionPane.showMessageDialog(this.getRootPane(), "Content is too long (" + str.length() + " chars > "+ (10 * 1024 * 1024) + " chars) " , "Can Not Paste", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                super.paste();
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setUndoManager(UndoManager undoManager) {
        this.getDocument().removeUndoableEditListener(undoManager);
        this.undoManager = undoManager;
        this.getDocument().addUndoableEditListener(undoManager);
    }

    public void undo() {
        if (this.undoManager.canUndo())
            this.undoManager.undo();
    }

    public void redo() {
        if (this.undoManager.canRedo())
            this.undoManager.redo();
    }

    @Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        if (b) this.discardAllEdits();
    }

    @Override
    public void setText(String t) {
        if (Objects.equals(t, getText()))
            return;
        String[] texts = t.split(System.lineSeparator());
        for (String s : texts) {
            if (s.length() >= 4096) {
                setLineWrap(true);
                setWrapStyleWord(true);
                break;
            }
        }
        try {
            getDocument().remove(0, getText().length());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        final int MAX = 8192;
        if (t.length() <= 8192)
            super.setText(t);
        else {
            super.setText(t.substring(0, MAX));
            String text = t.substring(MAX);
            System.out.println(Runtime.getRuntime().freeMemory() / 256);
            final int BLOCK_SIZE = (int) (Runtime.getRuntime().freeMemory() / 256);
            LoadingDialog loadingDialog = new LoadingDialog();
            loadingDialog.setParentWindow(getRootPane());
            loadingDialog.showDialog();
            loadingDialog.setMinAndMax(0, text.length());
            for (int i = 0; i < text.length();i = Math.min(text.length(), i+BLOCK_SIZE)) {
                System.out.println(i + "/" + text.length());
                loadingDialog.updateProgress(i, i + "/" + text.length());
                try {
                    synchronized (TextArea.this) {
                        TextArea.this.getDocument().insertString(getText().length(), text.substring(i, Math.min(text.length(), i+BLOCK_SIZE)), null);
                    }
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
            loadingDialog.closeDialog();
            getRootPane().setFocusable(true);
        }
        discardAllEdits();
    }

    public void gotoLineAndColumn() {
        Point caretPos = getCaretPos();
        Object object = JOptionPane.showInputDialog(getRootPane(), Main.language.getLanguage("textarea.gotoLineAndColumn.message"), Main.language.getLanguage("textarea.gotoLineAndColumn.title"), JOptionPane.PLAIN_MESSAGE, null, null, caretPos.x + ":" + caretPos.y);
        if (object != null) {
            String string = object.toString();
            if (Pattern.matches("\\s*\\d+\\s*([:：])\\s*\\d+\\s*", string)) {
                String[] inputs = string.replace(" ", "").replace("：", ":").split(":");
                setCaretPos(new Point(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1])));
            }
        }
    }

    private void discardAllEdits() {
        if (this.undoManager != null)
            this.undoManager.discardAllEdits();
    }
}
