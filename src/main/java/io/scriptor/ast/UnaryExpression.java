package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class UnaryExpression extends Expression {

    public final String operator;
    public final Expression expression;

    public UnaryExpression(final SourceLocation location, final String operator, final Expression expression) {
        super(location);

        assert operator != null;
        assert expression != null;

        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("%s%s", operator, expression);
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant();
    }

    @Override
    public Value evaluate(final Env env) {
        final var value = expression.evaluate(env);
        final var op = env.getUnaryOperator(location, operator, value.getType(location));
        return op.evaluate(value);
    }
}
