package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class ConstExpression extends Expression {

    public static Expression makeConst(final Expression expression) {
        if (expression.isConstant())
            return new ConstExpression(expression);
        return expression;
    }

    public final Value value;

    private ConstExpression(final Expression expression) {
        assert expression.isConstant();
        this.value = expression.evaluate(new Env());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Value evaluate(final Env env) {
        return value;
    }
}
