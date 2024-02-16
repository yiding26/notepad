package yiding.log;

import java.util.ArrayList;
import java.util.Stack;

public class Variable {
    String name;
    String content;
    ArrayList<String> attributes;
    int index, len;
    public void check() {
        if (!hasAttribute()) return;
        String str;
        attributes = new ArrayList<>();
        str = content.substring(content.indexOf("{"));
        if (anyBraceError(str) != -1) throw new RuntimeException(str + System.lineSeparator() + "Expected '{' or '}' at "+ (anyBraceError(str) + 1));
        for (int i = 0;i < str.length();i++) {
            i = str.indexOf("{", i) + 1;
            int len = 0;
            char[] chars = str.toCharArray();
            for (int j = i;j<str.length() && chars[j] != '}';j++) len++;
            attributes.add(str.substring(i, i+len));
            if (str.indexOf("{", i) == -1) break;
        }
    }

    private int anyBraceError(String str) {
        Stack<Character> stack = new Stack<>();
        for(int i = 0;i < str.length();i++){
            char c = str.charAt(i);
            if (c == '{') stack.push(c);
            else {
                if(stack.isEmpty())
                    return i;
                char topChar = stack.peek();
                if(c == '}')
                    if (topChar != '{') return i;
                    else stack.pop();
            }
        }
        if(!stack.isEmpty())
            return str.length() - 1;
        return -1;
    }

    public static Variable[] getVariables(String str) {
        if (!str.contains("%")) return null;
        ArrayList<Variable> variables = new ArrayList<>();
        for (int i = 0; i<str.length();i++) {
            i = str.indexOf("%", i) + 1;
            Variable variable = new Variable();
            int len = 0;
            char[] chars = str.toCharArray();
            boolean flag = false;
            for (int j = i;j<str.length();j++) {
                if (chars[j] == '{') flag = true;
                if (!flag)
                    if (chars[j] == ' ' || chars[j] == ')' || chars[j] == ']' || chars[j] == ':' || chars[j] == '%')
                        break;
                len++;
                if (flag && chars[j] == '}') break;
            }
            variable.name = str.substring(i, i+len);
            variable.content = "%" + variable.name;
            if (variable.hasAttribute()) variable.name = variable.name.substring(0, variable.name.indexOf("{"));
            variable.index = i - 1;
            variable.len = len;
            variable.check();
            variables.add(variable);
            if (str.indexOf("%", i) == -1) break;
        }
        return variables.toArray(new Variable[] {});
    }

    public boolean hasAttribute() {
        return content.contains("{");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
