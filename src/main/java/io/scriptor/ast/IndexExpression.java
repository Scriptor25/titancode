package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class IndexExpression extends Expression {

    public final Expression expression;
    public final Expression index;

    public IndexExpression(final Expression expression, final Expression index) {
        assert expression != null;
        assert index != null;

        this.expression = expression;
        this.index = index;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", expression, index);
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant() && index.isConstant();
    }

    @Override
    public Value evaluate(final Env env) {
        final var eindex = index.evaluate(env);
        final var value = expression.evaluate(env);

        final var i = eindex.getInt();
        final var result = value.getAt(i);
        assert result != null;
        return result;
    }
}
