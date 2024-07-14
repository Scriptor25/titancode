package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.ast.Expression;

public class Env {

    private final Env parent;
    private final Env global;
    private final Map<String, Map<Integer, Function>> functions = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();

    private Value[] varargs;

    public Env() {
        this.parent = null;
        this.global = this;
    }

    public Env(final Env parent) {
        assert parent != null;
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

        if (functions.containsKey(name) && functions.get(name).containsKey(args.length))
            throw new RuntimeException("function does already exist");

        functions
                .computeIfAbsent(name, key -> new HashMap<>())
                .put(args.length, new Function(name, args, varargs, expression));
    }

    public Function getFunction(final String name, final int args) {
        assert name != null;

        if (functions.containsKey(name)) {
            if (functions.get(name).containsKey(args))
                return functions.get(name).get(args);
            for (final var entry : functions.get(name).entrySet()) {
                final var function = entry.getValue();
                if (args < function.args.length)
                    continue;
                if (args > function.args.length && !function.varargs)
                    continue;
                return function;
            }
        }

        if (!hasParent())
            throw new RuntimeException("no such function");

        return parent.getFunction(name, args);
    }

    public RBinaryOperator getBinaryOperator(final String operator, final Type lhs, final Type rhs) {
        assert operator != null;
        assert lhs != null;
        assert rhs != null;

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

                        case "&", "&=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getLong() & r.getLong()),
                                    operator.equals("&="));
                        }
                        case "|", "|=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getLong() | r.getLong()),
                                    operator.equals("|="));
                        }
                        case "^", "^=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getLong() ^ r.getLong()),
                                    operator.equals("^="));
                        }

                        case "<<", "<<=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getLong() << r.getLong()),
                                    operator.equals("<<="));
                        }
                        case ">>", ">>=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getLong() >> r.getLong()),
                                    operator.equals(">>="));
                        }
                        case ">>>", ">>>=" -> {
                            return new RBinaryOperator(
                                    (l, r) -> new NumberValue(l.getLong() >>> r.getLong()),
                                    operator.equals(">>>="));
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

                throw new RuntimeException("no such binary operator");
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

                throw new RuntimeException("no such unary operator");
            }
        }
    }

    public void defineVariable(final String name, final Value value) {
        assert name != null;
        assert value != null;

        if (variables.containsKey(name))
            throw new RuntimeException("variable does already exist");

        variables.put(name, new Variable(name, value));
    }

    public Variable getVariable(final String name) {
        assert name != null;

        if (variables.containsKey(name))
            return variables.get(name);

        if (!hasParent())
            throw new RuntimeException("no such variable");

        return parent.getVariable(name);
    }

    public Variable setVariable(final String name, final Value value) {
        assert name != null;
        assert value != null;

        final var variable = getVariable(name);
        variable.value = value;
        return variable;
    }

    public Value getVarArgs() {
        if (varargs == null) {
            if (!hasParent())
                throw new RuntimeException("no var args in this environment");

            return parent.getVarArgs();
        }

        return new ArrayValue(varargs);
    }

    public Value call(final String name, final Value[] args) {
        assert name != null;
        assert args != null;

        final var function = getFunction(name, args.length);
        final var env = new Env(global);

        assert args.length >= function.args.length;
        assert function.varargs || args.length == function.args.length;

        if (args.length < function.args.length)
            throw new RuntimeException("not enough arguments");

        if (!function.varargs && args.length > function.args.length)
            throw new RuntimeException("too many arguments");

        int i = 0;
        for (; i < function.args.length; ++i)
            env.defineVariable(function.args[i], args[i]);

        final var f = i;
        env.varargs = new Value[args.length - f];
        for (; i < args.length; ++i)
            env.varargs[i - f] = args[i];

        return function.expression.evaluate(env);
    }

    @SuppressWarnings("unchecked")
    public <R> R call(final String name, final Object... args) {
        assert name != null;
        assert args != null;

        final var function = getFunction(name, args.length);
        final var env = new Env(global);

        assert args.length >= function.args.length;
        assert function.varargs || args.length == function.args.length;

        if (args.length < function.args.length)
            throw new RuntimeException("not enough arguments");

        if (!function.varargs && args.length > function.args.length)
            throw new RuntimeException("too many arguments");

        int i = 0;
        for (; i < function.args.length; ++i)
            env.defineVariable(function.args[i], Value.fromJava(args[i]));

        final var f = i;
        env.varargs = new Value[args.length - f];
        for (; i < args.length; ++i)
            env.varargs[i - f] = Value.fromJava(args[i]);

        return (R) function.expression.evaluate(env).getValue();
    }
}
