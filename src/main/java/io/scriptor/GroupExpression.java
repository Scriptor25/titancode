package io.scriptor;

public class GroupExpression extends Expression {

    public final Expression[] expressions;

    public GroupExpression(final Expression[] expressions) {
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append("(\n");
        for (final var expression : expressions)
            builder.append(expression).append("\n");
        builder.append(")");
        return builder.toString();
    }

    @Override
    public Value evaluate(final Env env) {
        throw new UnsupportedOperationException();
    }
}
