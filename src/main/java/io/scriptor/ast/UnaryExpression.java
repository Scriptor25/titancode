package io.scriptor.ast;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class UnaryExpression extends Expression {

    public final String operator;
    public final Expression expression;

    public UnaryExpression(final RLocation location, final String operator, final Expression expression) {
        super(location);

        assert operator != null;
        assert expression != null;

        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant();
    }

    @Override
    public Value evaluate(final Env env) {
        final var value = expression.evaluate(env);
        final var op = env.getUnaryOperator(operator, value.getType());
        return op.evaluate(value);
    }
}
