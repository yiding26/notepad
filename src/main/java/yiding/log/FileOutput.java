package yiding.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOutput extends Output{
    final String format;
    BufferedWriter out;
    File file;
    Boolean append;
    public FileOutput(String format, File file, boolean append) {
        super(format);
        this.format = format;
        this.file = file;
        this.append = append;
        try {
            out = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), append));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String format(String level, String msg, Logger logger, StackTraceElement stackTraceElement) {
        return super.format(level, msg, logger, stackTraceElement);
    }

    @Override
    public void output(String content) {
        try {
            out = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
            out.append(content);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
