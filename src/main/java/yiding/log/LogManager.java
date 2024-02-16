package yiding.log;

import java.util.ArrayList;

public class LogManager {
    public static ArrayList<Output> outputs = new ArrayList<>();
    public static void add(Output output) {
        Variable.getVariables(output.format);
        outputs.add(output);
    }
    public static void remove(Output output) {
        outputs.remove(output);
    }
    public static void remove(int i) {
        outputs.remove(i);
    }
    protected static ArrayList<Output> getOutputs() {
        return outputs;
    }
}
