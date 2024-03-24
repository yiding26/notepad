package yiding.notepad.view.component;

import yiding.NotepadApplication;
import yiding.notepad.utils.Language;
import yiding.notepad.view.LoadingDialog;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.BeanProperty;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class TextArea extends JTextPane {
    UndoManager undoManager = new UndoManager();
    public TextArea() {
        super();
        setUndoManager(undoManager);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
    }

    public Point getCaretPos() {
        int pos = getCaretPosition();
        int line, column;
        try {
            line = getLineOfOffset(pos);
            column = pos - getLineStartOffset(line);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        return new Point(line + 1, column + 1);
    }

    public void setCaretPos(Point point) {
        int line = point.x - 1, column = point.y - 1, index = 0;
        try {
            final int maxLine = getLineCount();
            if (line > maxLine)
                line = maxLine;
            final int maxColumn = getLineEndOffset(line) - getLineStartOffset(line);
            if (column > maxColumn)
                column = maxColumn;
            index += getLineStartOffset(line) + column;
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        setCaretPosition(index);
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
                    JOptionPane.showMessageDialog(getRootPane(), "Content is too long (" + str.length() + " chars > "+ (10 * 1024 * 1024) + " chars) " , "Can Not Paste", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                super.paste();
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setUndoManager(UndoManager undoManager) {
        getDocument().removeUndoableEditListener(undoManager);
        this.undoManager = undoManager;
        getDocument().addUndoableEditListener(undoManager);
    }

    public void undo() {
        if (undoManager.canUndo())
            undoManager.undo();
    }

    public void redo() {
        if (undoManager.canRedo())
            undoManager.redo();
    }

    @Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        if (b) discardAllEdits();
    }

    @Override
    public void setText(String t) {
        if (Objects.equals(t, getText()))
            return;
        String[] texts = t.split(System.lineSeparator());
        for (String s : texts) {
            if (s.length() >= 4096) {
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
            final int BLOCK_SIZE = 1024 * 1024;
            LoadingDialog loadingDialog = new LoadingDialog();
            loadingDialog.setTitle("Loading...");
            loadingDialog.setParentWindow(getRootPane());
            loadingDialog.showDialog();
            loadingDialog.setMinAndMax(0, text.length());
            for (int i = 0; i < text.length();i = Math.min(text.length(), i+BLOCK_SIZE)) {
                double d = ((double) (i + 1) / (double) text.length()) * 100;
                NotepadApplication.logger.info(i + "/" + text.length() + " " + d + "%");
                loadingDialog.updateProgress(i, i + "/" + text.length() + "  " + (int) d + "%");
                if (loadingDialog.isCanceled())
                    break;
                try {
                    synchronized (TextArea.this) {
                        getDocument().insertString(getText().length(), text.substring(i, Math.min(text.length(), i+BLOCK_SIZE)), null);
                    }
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
            loadingDialog.setCloseMode(AbstractDialog.DISPOSE);
            loadingDialog.closeDialog();
            getRootPane().setFocusable(true);
        }
        discardAllEdits();
    }

    public void gotoLAC() {
        Point caretPos = getCaretPos();
        Object object = JOptionPane.showInputDialog(getRootPane(), Language.getLanguage("textarea.gotoLAC.message"), Language.getLanguage("textarea.gotoLAC.title"), JOptionPane.PLAIN_MESSAGE, null, null, caretPos.x + ":" + caretPos.y);
        if (object != null) {
            String string = object.toString();
            if (Pattern.matches("\\s*\\d+\\s*([:：])\\s*\\d+\\s*", string)) {
                String[] inputs = string.replace(" ", "").replace("：", ":").split(":");
                setCaretPos(new Point(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1])));
            }
        }
    }

    public void discardAllEdits() {
        if (undoManager != null)
            undoManager.discardAllEdits();
    }

    @BeanProperty(bound = false)
    public int getLineCount() {
        Element map = getDocument().getDefaultRootElement();
        return map.getElementCount();
    }

    public int getLineStartOffset(int line) throws BadLocationException {
        int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength()+1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    public int getLineEndOffset(int line) throws BadLocationException {
        int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength()+1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            Element lineElem = map.getElement(line);
            int endOffset = lineElem.getEndOffset();
            // hide the implicit break at the end of the document
            return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
        }
    }

    public int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength()+1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }
}
