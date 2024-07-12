package io.scriptor.ast;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class GroupExpression extends Expression {

    public final Expression[] expressions;

    public GroupExpression(final RLocation location, final Expression[] expressions) {
        super(location);

        assert expressions != null;

        this.expressions = expressions;
    }

    @Override
    public String toString() {
        if (expressions.length == 1)
            return String.format("(%s)", expressions[0]);

        final var builder = new StringBuilder();
        final var spaces = new StringBuilder();
        for (int i = 0; i < depth; ++i)
            spaces.append("  ");
        ++depth;
        builder.append("(\n");
        for (final var expression : expressions)
            builder.append(spaces).append("  ").append(expression).append('\n');
        builder.append(spaces).append(')');
        --depth;
        return builder.toString();
    }

    @Override
    public boolean isConstant() {
        for (final var expression : expressions)
            if (!expression.isConstant())
                return false;
        return true;
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        Value result = null;
        final var env1 = new Env(env);
        for (final var expression : expressions) {
            result = expression.evaluate(env1);
        }
        return result;
    }
}
