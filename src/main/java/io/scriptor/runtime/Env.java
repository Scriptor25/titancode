package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.ast.Expression;

public class Env {

    private final Env parent;
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();

    public Env() {
        this.parent = null;
    }

    public Env(final Env parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Env getParent() {
        return parent;
    }

    public void defineFunction(final String name, final String[] args, final Expression expression) {
        functions.put(name, new Function(name, args, expression));
    }

    public Function getFunction(final String name) {
        if (functions.containsKey(name))
            return functions.get(name);
        if (hasParent())
            return parent.getFunction(name);
        throw new IllegalStateException(String.format("undefined function '%s'", name));
    }

    public IOperator getOperator(final String operator, final Type lhs, final Type rhs) {
        if (lhs == Type.getNumber() && rhs == Type.getNumber()) {
            return switch (operator) {
                case "+" -> (l, r) -> new NumberValue((Double) l.getValue() + (Double) r.getValue());
                case "-" -> (l, r) -> new NumberValue((Double) l.getValue() - (Double) r.getValue());
                case "*" -> (l, r) -> new NumberValue((Double) l.getValue() * (Double) r.getValue());
                case "/" -> (l, r) -> new NumberValue((Double) l.getValue() / (Double) r.getValue());
                case "%" -> (l, r) -> new NumberValue((Double) l.getValue() % (Double) r.getValue());

                default -> throw new IllegalStateException();
            };
        }
        throw new IllegalStateException(String.format("undefined operator '%s %s %s'", lhs.name, operator, rhs.name));
    }

    public void defineVariable(final String name, final Value value) {
        if (variables.containsKey(name))
            throw new IllegalStateException(String.format("redefining variable '%s'", name));
        variables.put(name, new Variable(name, value));
    }

    public Variable getVariable(final String name) {
        if (variables.containsKey(name))
            return variables.get(name);
        if (hasParent())
            return parent.getVariable(name);
        throw new IllegalStateException(String.format("undefined variable '%s'", name));
    }

    public Variable setVariable(final String name, final Value value) {
        if (variables.containsKey(name)) {
            final var variable = variables.get(name);
            variable.value = value;
            return variable;
        }
        if (hasParent())
            return parent.setVariable(name, value);
        throw new IllegalStateException(String.format("undefined variable '%s'", name));
    }

    public Value call(final String name, final Value[] args) {
        final var function = getFunction(name);
        final var env = new Env(this);
        for (int i = 0; i < args.length; ++i)
            env.defineVariable(function.args[i], args[i]);

        return function.expression.evaluate(env);
    }

    @SuppressWarnings("unchecked")
    public <R> R call(final String name, final Object... args) {
        final var function = getFunction(name);
        final var env = new Env(this);
        for (int i = 0; i < args.length; ++i)
            env.defineVariable(function.args[i], Value.fromJava(args[i]));

        final var result = function.expression.evaluate(env);
        return (R) result.getValue();
    }
}
