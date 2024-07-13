package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.ast.Expression;

public class Env {

    private final Env parent;
    private final Env global;
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();
    private Value[] varargs;

    public Env() {
        this.parent = null;
        this.global = this;
    }

    public Env(final Env parent) {
        assert parent != null;

        if (parent == null)
            throw new IllegalStateException("parent must not be null");

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
            final String name,
            final String[] args,
            final boolean varargs,
            final Expression expression) {
        assert name != null;
        assert args != null;
        assert expression != null;
        assert !functions.containsKey(name);

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (args == null)
            throw new IllegalStateException("args must not be null");

        if (expression == null)
            throw new IllegalStateException("expression must not be null");

        if (functions.containsKey(name))
            throw new IllegalStateException("function does already exist");

        functions.put(name, new Function(name, args, varargs, expression));
    }

    public Function getFunction(final String name) {
        assert name != null;

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (functions.containsKey(name))
            return functions.get(name);

        assert hasParent();

        if (!hasParent())
            throw new IllegalStateException("no such function");

        return parent.getFunction(name);
    }

    public RBinaryOperator getBinaryOperator(final String operator, final Type lhs, final Type rhs) {
        assert operator != null;
        assert lhs != null;
        assert rhs != null;

        if (operator == null)
            throw new IllegalStateException("operator must not be null");

        if (lhs == null)
            throw new IllegalStateException("lhs must not be null");

        if (rhs == null)
            throw new IllegalStateException("rhs must not be null");

        switch (operator) {
            case "==" -> {
                return new RBinaryOperator((l, r) -> new NumberValue(l.getValue().equals(r.getValue())), false);
            }

            case "&&" -> {
                return new RBinaryOperator((l, r) -> new NumberValue(l.getBoolean() && r.getBoolean()), false);
            }

            case "||" -> {
                return new RBinaryOperator((l, r) -> new NumberValue(l.getBoolean() || r.getBoolean()), false);
            }

            default -> {
                if (lhs == Type.getNumber() && rhs == Type.getNumber()) {
                    switch (operator) {
                        case "+", "+=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() + r.getDouble()),
                                    operator.equals("+="));
                        }
                        case "-", "-=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() - r.getDouble()),
                                    operator.equals("-="));
                        }
                        case "*", "*=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() * r.getDouble()),
                                    operator.equals("*="));
                        }
                        case "/", "/=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() / r.getDouble()),
                                    operator.equals("/="));
                        }
                        case "%", "%=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() % r.getDouble()),
                                    operator.equals("%="));
                        }

                        case "<" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() < r.getDouble()),
                                    false);
                        }

                        case ">" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() > r.getDouble()),
                                    false);
                        }

                        case "<=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() <= r.getDouble()),
                                    false);
                        }

                        case ">=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getDouble() >= r.getDouble()),
                                    false);
                        }
                    }
                }

                throw new IllegalStateException(String.format(
                        "no such binary operator '%s %s %s'",
                        lhs.name,
                        operator,
                        rhs.name));
            }
        }
    }

    public IUnaryOperator getUnaryOperator(final String operator, final Type type) {
        assert operator != null;
        assert type != null;

        switch (operator) {
            case "!" -> {
                return v -> new NumberValue(!v.getBoolean());
            }

            default -> {
                if (type == Type.getNumber()) {
                    switch (operator) {
                        case "-" -> {
                            return v -> new NumberValue(-v.getDouble());
                        }
                    }
                }

                throw new IllegalStateException(
                        String.format(
                                "no such unary operator '%s%s'",
                                operator,
                                type.name));
            }
        }
    }

    public void defineVariable(final String name, final Value value) {
        assert name != null;
        assert value != null;
        assert !variables.containsKey(name);

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (value == null)
            throw new IllegalStateException("value must not be null");

        if (variables.containsKey(name))
            throw new IllegalStateException("variable does already exist");

        variables.put(name, new Variable(name, value));
    }

    public Variable getVariable(final String name) {
        assert name != null;

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (variables.containsKey(name))
            return variables.get(name);

        assert hasParent();

        if (!hasParent())
            throw new IllegalStateException("no such variable");

        return parent.getVariable(name);
    }

    public Variable setVariable(final String name, final Value value) {
        assert name != null;
        assert value != null;

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (value == null)
            throw new IllegalStateException("value must not be null");

        final var variable = getVariable(name);
        variable.value = value;
        return variable;
    }

    public Value getVarargs() {
        if (varargs == null) {
            assert hasParent();

            if (!hasParent())
                throw new IllegalStateException("no varargs in this environment");

            return parent.getVarargs();
        }

        return new ArrayValue(varargs);
    }

    public Value getVararg(final Value index) {
        assert index != null;

        if (varargs == null) {
            assert hasParent();

            if (!hasParent())
                throw new IllegalStateException("no varargs in this environment");

            return parent.getVararg(index);
        }

        final var idx = index.getInt();
        return varargs[idx];
    }

    public Value call(final String name, final Value[] args) {
        assert name != null;
        assert args != null;

        final var function = getFunction(name);
        final var env = new Env(global);

        assert args.length >= function.args.length;

        if (args.length < function.args.length)
            throw new IllegalStateException("not enough arguments");

        int i = 0;
        for (; i < function.args.length; ++i)
            env.defineVariable(function.args[i], args[i]);

        assert function.varargs || i >= args.length;

        if (!function.varargs && i < args.length)
            throw new IllegalStateException("too many arguments");

        final var f = i;
        env.varargs = new Value[args.length - f];
        for (; i < args.length; ++i)
            env.varargs[i - f] = args[i];

        return function.expression.evaluate(env);
    }

    @SuppressWarnings("unchecked")
    public <R> R call(final String name, final Object... args) {
        final var function = getFunction(name);
        final var env = new Env(global);

        assert args.length >= function.args.length;

        if (args.length < function.args.length)
            throw new IllegalStateException("not enough arguments");

        int i = 0;
        for (; i < function.args.length; ++i)
            env.defineVariable(function.args[i], Value.fromJava(args[i]));

        assert function.varargs || i >= args.length;

        if (!function.varargs && i < args.length)
            throw new IllegalStateException("too many arguments");

        final var f = i;
        env.varargs = new Value[args.length - f];
        for (; i < args.length; ++i)
            env.varargs[i - f] = Value.fromJava(args[i]);

        final var result = function.expression.evaluate(env);
        return (R) result.getValue();
    }
}
