package io.scriptor.ast;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class DefFunctionExpression extends Expression {

    public final String name;
    public final String[] args;
    public final boolean varargs;
    public final Expression expression;

    public DefFunctionExpression(
            final RLocation location,
            final String name,
            final String[] args,
            final boolean varargs,
            final Expression expression) {
        super(location);

        assert name != null;
        assert args != null;
        assert expression != null;

        this.name = name;
        this.args = args;
        this.varargs = varargs;
        this.expression = expression;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("def ")
                .append(name)
                .append('(');

        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }

        return builder
                .append(") = ")
                .append(expression)
                .toString();
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        env.defineFunction(name, args, varargs, expression);
        return null;
    }
}
