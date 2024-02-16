package yiding.log;

import java.text.SimpleDateFormat;

public class Output{
    final String format;
    public Output(String format) {
        this.format = format;
    }

    public String format(String level, String msg, Logger logger, StackTraceElement stackTraceElement) {
        String text = format;
        Variable[] variables = Variable.getVariables(text);
        if (variables == null) return "";
        for (Variable variable : variables) {
            String replacement = switch (variable.name) {
                case "d", "date" -> {
                    String format;
                    if (variable.hasAttribute()) format = variable.attributes.getFirst();
                    else format = "yyyy-MM-dd HH:mm:ss";
                    yield new SimpleDateFormat(format).format(System.currentTimeMillis());
                }
                case "c" -> {
                    String className = stackTraceElement.getClassName();
                    if (variable.hasAttribute()) {
                        String content = variable.attributes.getFirst();
                        if (content.matches("(-)?(\\d)+")) {
                            int i = Integer.parseInt(content);
                            if (i != 0) {
                                String[] classNames = className.split("\\.");
                                StringBuilder classNameBuilder = new StringBuilder();
                                if (i > 0) {
                                    if (i > classNames.length) i = classNames.length;
                                    i = classNames.length - i;
                                } else {
                                    i = -1 * i;
                                    if (i > classNames.length) i = 0;
                                }
                                for (int j = i; j<classNames.length; j++) {
                                    classNameBuilder.append(classNames[j]);
                                    if (j == classNames.length - 1) break;
                                    else classNameBuilder.append(".");
                                }
                                className = classNameBuilder.toString();
                            }
                        }
                    }
                    yield className;
                }
                case "l", "logger" -> logger.name;
                case "L", "line" -> String.valueOf(stackTraceElement.getLineNumber());
                case "level" -> level;
                case "m", "msg", "message" -> msg;
                case "M", "method" -> stackTraceElement.getMethodName();
                case "n" -> System.lineSeparator();
                case "t", "thread" -> Thread.currentThread().getName();
                default -> "";
            };
            text = text.replace(variable.content, replacement);
        }
        return text;
    }

    public void output(String content) {

    }
}
