package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.ast.Expression;

public class Env {

    private final Env parent;
    private final Env global;
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();
    private IValue[] varargs;

    public Env() {
        this.parent = null;
        this.global = this;
    }

    public Env(final Env parent) {
        this.parent = parent;
        this.global = parent.global;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Env getParent() {
        return parent;
    }

    public Env getGlobal() {
        return global;
    }

    public void defineFunction(
            final String name, final String[] args, final boolean varargs,
            final Expression expression) {
        functions.put(name, new Function(name, args, varargs, expression));
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

    public void defineVariable(final String name, final IValue value) {
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

    public Variable setVariable(final String name, final IValue value) {
        if (variables.containsKey(name)) {
            final var variable = variables.get(name);
            variable.value = value;
            return variable;
        }
        if (hasParent())
            return parent.setVariable(name, value);
        throw new IllegalStateException(String.format("undefined variable '%s'", name));
    }

    public IValue getAllVarargs() {
        if (varargs == null) {
            if (hasParent())
                return parent.getAllVarargs();
            throw new IllegalStateException("no varargs in this environment");
        }

        return new ArrayValue(varargs);
    }

    public IValue getVararg(final IValue index) {
        if (varargs == null) {
            if (hasParent())
                return parent.getVararg(index);
            throw new IllegalStateException("no varargs in this environment");
        }

        final var idx = (int) (double) (Double) index.getValue();
        return varargs[idx];
    }

    public IValue call(final String name, final IValue[] args) {
        final var function = getFunction(name);
        final var env = new Env(global);

        if (args.length < function.args.length)
            throw new IllegalStateException("not enought arguments");

        int i = 0;
        for (; i < function.args.length; ++i)
            env.defineVariable(function.args[i], args[i]);

        if (!function.varargs && i < args.length)
            throw new IllegalStateException("too many arguments");

        final var f = i;
        env.varargs = new IValue[args.length - f];
        for (; i < args.length; ++i)
            env.varargs[i - f] = args[i];

        return function.expression.evaluate(env);
    }

    @SuppressWarnings("unchecked")
    public <R> R call(final String name, final Object... args) {
        final var function = getFunction(name);
        final var env = new Env(global);

        if (args.length < function.args.length)
            throw new IllegalStateException("not enought arguments");

        int i = 0;
        for (; i < function.args.length; ++i)
            env.defineVariable(function.args[i], IValue.fromJava(args[i]));

        if (!function.varargs && i < args.length)
            throw new IllegalStateException("too many arguments");

        final var f = i;
        env.varargs = new IValue[args.length - f];
        for (; i < args.length; ++i)
            env.varargs[i - f] = IValue.fromJava(args[i]);

        final var result = function.expression.evaluate(env);
        return (R) result.getValue();
    }
}
