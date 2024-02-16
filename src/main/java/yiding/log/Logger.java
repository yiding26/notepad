package yiding.log;

import java.util.ArrayList;

public class Logger {
    public ArrayList<Output> outputs = new ArrayList<>();
    public void add(Output output) {
        Variable.getVariables(output.format);
        outputs.add(output);
    }
    public void remove(Output output) {
        outputs.remove(output);
    }
    public void remove(int i) {
        outputs.remove(i);
    }
    protected ArrayList<Output> getOutputs() {
        return outputs;
    }
    String name;
    public Logger(String name) {
        this.name = name;
    }
    private void no(String level, String message) {
        ArrayList<Output> outputs = LogManager.getOutputs();
        for (Output output : outputs)
            output.output(output.format(level, message, this, Thread.currentThread().getStackTrace()[3]));
        for (Output output : this.outputs)
            output.output(output.format(level, message, this, Thread.currentThread().getStackTrace()[3]));
    }
    public void info(String message) {
        no("info", message);
    }

    public void warn(String message) {
        no("warn", message);
    }

    public void error(String message) {
        no("error", message);
    }
}