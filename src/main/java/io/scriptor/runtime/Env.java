package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.TitanException;

public class Env {

    private final Env parent;
    private final Env global;
    private final Value[] varargs;

    private final Map<Name, Map<Integer, IFunction>> functions = new HashMap<>();
    private final Map<Name, Variable> variables = new HashMap<>();

    public Env() {
        this.parent = null;
        this.global = this;
        this.varargs = null;
    }

    public Env(final Env parent) {
        assert parent != null;
        this.parent = parent;
        this.global = parent.global;
        this.varargs = null;
    }

    public Env(final Env parent, final Value[] varargs) {
        assert parent != null;
        this.parent = parent;
        this.global = parent.global;
        this.varargs = varargs;
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

    public void defineFunction(final IFunction function) {
        assert function != null;

        if (functions.containsKey(function.name()) && functions.get(function.name()).containsKey(function.argCount()))
            throw new TitanException(
                    function.location(),
                    "function does already exist: %s(%d%s)",
                    function.name(),
                    function.argCount(),
                    function.hasVarArgs() ? ", ?" : "");

        functions
                .computeIfAbsent(function.name(), key -> new HashMap<>())
                .put(function.argCount(), function);
    }

    public IFunction getFunction(
            final SourceLocation location,
            final Name name,
            final int argCount) {
        assert name != null;

        if (functions.containsKey(name)) {
            if (functions.get(name).containsKey(argCount))
                return functions.get(name).get(argCount);
            for (final var entry : functions.get(name).entrySet()) {
                final var function = entry.getValue();
                if (argCount < function.argCount())
                    continue;
                if (argCount > function.argCount() && !function.hasVarArgs())
                    continue;
                return function;
            }
        }

        if (!hasParent())
            throw new TitanException(
                    location,
                    "no such function: %s(%d)",
                    name,
                    argCount);

        return parent.getFunction(location, name, argCount);
    }

    public BinOpInfo getBinaryOperator(
            final SourceLocation location,
            final String operator,
            final Type lhs,
            final Type rhs) {
        assert location != null;
        assert operator != null;
        assert lhs != null;
        assert rhs != null;

        switch (operator) {
            case "==" -> {
                return new BinOpInfo(
                        (l, r) -> new NumberValue(location, l.getValue().equals(r.getValue())),
                        false);
            }

            case "&&" -> {
                return new BinOpInfo(
                        (l, r) -> new NumberValue(location, l.getBoolean() && r.getBoolean()),
                        false);
            }

            case "||" -> {
                return new BinOpInfo(
                        (l, r) -> new NumberValue(location, l.getBoolean() || r.getBoolean()),
                        false);
            }

            default -> {
                if (lhs == Type.getNumber(location) && rhs == Type.getNumber(location)) {
                    switch (operator) {
                        case "+", "+=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() + r.getDouble()),
                                    operator.equals("+="));
                        }
                        case "-", "-=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() - r.getDouble()),
                                    operator.equals("-="));
                        }
                        case "*", "*=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() * r.getDouble()),
                                    operator.equals("*="));
                        }
                        case "/", "/=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() / r.getDouble()),
                                    operator.equals("/="));
                        }
                        case "%", "%=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() % r.getDouble()),
                                    operator.equals("%="));
                        }

                        case "&", "&=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getLong() & r.getLong()),
                                    operator.equals("&="));
                        }
                        case "|", "|=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getLong() | r.getLong()),
                                    operator.equals("|="));
                        }
                        case "^", "^=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getLong() ^ r.getLong()),
                                    operator.equals("^="));
                        }

                        case "<<", "<<=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getLong() << r.getLong()),
                                    operator.equals("<<="));
                        }
                        case ">>", ">>=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getLong() >> r.getLong()),
                                    operator.equals(">>="));
                        }
                        case ">>>", ">>>=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getLong() >>> r.getLong()),
                                    operator.equals(">>>="));
                        }

                        case "<" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() < r.getDouble()),
                                    false);
                        }
                        case ">" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() > r.getDouble()),
                                    false);
                        }
                        case "<=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() <= r.getDouble()),
                                    false);
                        }
                        case ">=" -> {
                            return new BinOpInfo(
                                    (l, r) -> new NumberValue(location, l.getDouble() >= r.getDouble()),
                                    false);
                        }
                    }
                }

                throw new TitanException(
                        location,
                        "no such binary operator: %s%s%s",
                        lhs.name,
                        operator,
                        rhs.name);
            }
        }
    }

    public IUnOp getUnaryOperator(final SourceLocation location, final String operator, final Type type) {
        assert location != null;
        assert operator != null;
        assert type != null;

        switch (operator) {
            case "!" -> {
                return v -> new NumberValue(location, !v.getBoolean());
            }

            default -> {
                if (type == Type.getNumber(location)) {
                    switch (operator) {
                        case "-" -> {
                            return v -> new NumberValue(location, -v.getDouble());
                        }
                    }
                }

                throw new TitanException(location,
                        "no such unary operator: %s%s",
                        operator,
                        type.name);
            }
        }
    }

    public void defineVariable(final SourceLocation location, final Name name, final Value value) {
        assert location != null;
        assert name != null;
        assert value != null;

        if (variables.containsKey(name))
            throw new TitanException(location, "variable does already exist: %s", name);

        variables.put(name, new Variable(name, value));
    }

    public Variable getVariable(final SourceLocation location, final Name name) {
        assert name != null;

        if (variables.containsKey(name))
            return variables.get(name);

        if (!hasParent())
            throw new TitanException(location, "no such variable: %s", name);

        return parent.getVariable(location, name);
    }

    public Variable setVariable(final SourceLocation location, final Name name, final Value value) {
        assert location != null;
        assert name != null;
        assert value != null;

        final var variable = getVariable(location, name);
        variable.value = value;
        return variable;
    }

    public Value getVarArgs(final SourceLocation location) {
        assert location != null;

        if (varargs == null) {
            if (!hasParent())
                throw new TitanException(location, "no var args in this environment");

            return parent.getVarArgs(location);
        }

        return new ArrayValue(location, varargs);
    }

    public Value call(final SourceLocation location, final Name name, final Value[] args) {
        assert name != null;
        assert args != null;

        return getFunction(location, name, args.length)
                .call(global, args);
    }

    @SuppressWarnings("unchecked")
    public <R> R call(final String name, final Object... args) {
        assert name != null;
        assert args != null;

        final var location = SourceLocation.UNKNOWN;

        final var values = new Value[args.length];
        for (int i = 0; i < args.length; ++i)
            values[i] = Value.fromJava(location, args[i]);

        return (R) getFunction(location, Name.get(name), values.length)
                .call(global, values)
                .getValue();
    }
}
