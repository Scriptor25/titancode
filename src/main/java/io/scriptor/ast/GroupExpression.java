package io.scriptor.ast;

import java.util.Arrays;

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
        if (expressions.length == 0)
            return "()";

        if (expressions.length == 1)
            return String.format("(%s)", expressions[0]);

        final var builder = new StringBuilder()
                .append("(");

        ++depth;
        var spaces = getSpaces();

        for (final var expression : expressions) {
            builder.append('\n').append(spaces).append(expression);
        }

        --depth;
        spaces = getSpaces();

        return builder
                .append('\n')
                .append(spaces)
                .append(')')
                .toString();
    }

    @Override
    public boolean isConstant() {
        return !Arrays
                .stream(expressions)
                .anyMatch(expression -> !expression.isConstant());
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
