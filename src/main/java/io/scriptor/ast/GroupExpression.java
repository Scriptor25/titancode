package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class GroupExpression extends Expression {

    private final Expression[] body;

    public GroupExpression(final SourceLocation location, final Expression[] body) {
        super(location);
        assert body != null;
        this.body = body;
    }

    @Override
    public String toString() {
        if (body.length == 0)
            return "()";

        if (body.length == 1)
            return String.format("(%s)", body[0]);

        final var builder = new StringBuilder()
                .append("(");

        ++depth;
        var spaces = getSpaces();

        for (final var expression : body) {
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
                .stream(body)
                .anyMatch(expression -> !expression.isConstant());
    }

    @Override
    public Expression makeConstant() {
        for (int i = 0; i < body.length; ++i)
            body[i] = body[i].makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        Value result = null;
        final var env1 = new Environment(env);
        for (final var expression : body) {
            result = expression.evaluate(env1);
        }
        return result;
    }
}
