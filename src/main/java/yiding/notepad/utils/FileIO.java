package yiding.notepad.utils;

import yiding.NotepadApplication;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileIO {
    public File file;
    public String path;
    public Charset encode;
    boolean isChars;

    public FileIO(String path) {
        this.file = new File(path);
        this.path = path;
        this.encode = StandardCharsets.UTF_8;
        this.isChars = true;
    }

    public String read() {
        if (isChars) return readChars();
        else return readBytes();
    }

    public boolean write(String text) {
        if (this.isChars) return writeChars(text);
        else return writeBytes(text);
    }

    private String readChars() {
        isChars = true;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(new FileInputStream(path), encode);
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if (str.indexOf(65533) != -1) {
                    isChars = false;
                    return readBytes();
                }
                stringBuilder.append(str);
                stringBuilder.append(System.lineSeparator());
            }
            NotepadApplication.logger.info("read file (chars) " + this.path);
            return stringBuilder.toString().replaceAll("\\r", "");
        } catch (FileNotFoundException e) {
            NotepadApplication.logger.error(String.format("read file (chars) %s can not find file, so create new.", path));
            return "";
        }
    }

    private String readBytes() {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.path);
            StringBuilder stringBuilder = new StringBuilder();
            byte[] bytes = fileInputStream.readAllBytes();
            for (byte b : bytes) {
                stringBuilder.append(getHex(b));
            }
            NotepadApplication.logger.info(String.format("read file (bytes) %s", path));
            fileInputStream.close();
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            NotepadApplication.logger.info(String.format("read file (bytes) %s can not find file, so create new.", path));
            return "";
        } catch (IOException e) {
            NotepadApplication.logger.info(String.format("read file (bytes) %s %s", path, e.getMessage()));
            return null;
        }
    }

    private boolean writeChars(String text) {
        try {
            OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(this.path), encode);
            streamWriter.append(text);
            streamWriter.close();
        } catch (IOException e) {
            NotepadApplication.logger.error(String.format("write file (chars) %s %s", path, e.getMessage()));
            return false;
        }
        NotepadApplication.logger.info(String.format("write file (chars) %s", path));
        return true;
    }

    private boolean writeBytes(String text) {
        try {
            char[] chars = text.replaceAll("\\r", "").replaceAll("\\n", "").toCharArray();
            byte[] bytes = new byte[chars.length / 2];
            for (int i = 0;i<chars.length;i+=2) {
                bytes[i/2] = getByte(String.valueOf(chars[i]) + chars[i + 1]);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            NotepadApplication.logger.error(String.format("write file (bytes) %s %s", path, e.getMessage()));
            return false;
        }
        NotepadApplication.logger.info(String.format("write file (bytes) %s", path));
        return true;
    }

    private String getHex(int i) {
        String s = Integer.toHexString(i);
        if (s.length() == 1) s = "0" + s;
        if (s.length() > 2)
            s = s.substring(s.length() - 2);
        return s;
    }

    private byte getByte(String s) {
        return (byte) Integer.parseInt(s, 16);
    }

    public static String get(Frame parentWindow, String name, String path) {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            int mode;
            if (name.toLowerCase().contains("open")) mode = FileDialog.LOAD;
            else if (name.toLowerCase().contains("save")) mode = FileDialog.SAVE;
            else return "";
            FileDialog fileDialog = new FileDialog(parentWindow, name, mode);
            if (!path.isEmpty()) {
                fileDialog.setDirectory(path);
                fileDialog.setFile(path);
            }
            fileDialog.setVisible(true);
            if (fileDialog.getDirectory() != null && fileDialog.getFile() != null)
                return fileDialog.getDirectory() + fileDialog.getFile();
            else if (!path.isEmpty()) return path;
        } else {
            JFileChooser fileChooser = new JFileChooser();
            if (!path.isEmpty()) {
                fileChooser.setCurrentDirectory(new File(path));
                fileChooser.setSelectedFile(new File(path));
            }
            int option = fileChooser.showDialog(parentWindow, name);
            if (option == JFileChooser.APPROVE_OPTION)
                return fileChooser.getSelectedFile().getAbsolutePath();
            else if (option == JFileChooser.CANCEL_OPTION)
                if (!path.isEmpty()) return path;
        }
        return "";
    }
}
