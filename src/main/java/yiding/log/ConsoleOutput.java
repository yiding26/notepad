package yiding.log;

public class ConsoleOutput extends Output {
    public ConsoleOutput(String format) {
        super(format);
    }

    @Override
    public String format(String level, String msg, Logger logger, StackTraceElement stackTraceElement) {
        return super.format(level, msg, logger, stackTraceElement);
    }

    @Override
    public void output(String content) {
        System.out.print(content);
    }
}
