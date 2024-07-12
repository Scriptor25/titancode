package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class GroupExpression extends Expression {

    public final Expression[] expressions;

    public GroupExpression(final Expression[] expressions) {
        assert expressions != null;

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
    public IValue evaluate(final Env env) {
        assert env != null;

        IValue result = null;
        final var env1 = new Env(env);
        for (final var expression : expressions) {
            result = expression.evaluate(env1);
        }
        return result;
    }
}
