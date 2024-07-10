package io.scriptor;

import java.util.HashMap;
import java.util.Map;

public class Env {

    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Value> variables = new HashMap<>();

    public void defineFunction(final String name, final String[] args, final Expression expression) {
        functions.put(name, new Function(name, args, expression));
    }

    public IOperator getOperator(final String operator, final Type lhs, final Type rhs) {
        throw new UnsupportedOperationException("Unimplemented method 'getOperator'");
    }

    public Value getVariable(final String name) {
        if (variables.containsKey(name))
            return variables.get(name);
        throw new IllegalStateException(String.format("undefined variable '%s'", name));
    }
}
