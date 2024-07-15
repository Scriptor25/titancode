package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class IndexExpression extends Expression {

    public final Expression expression;
    public final Expression index;

    public IndexExpression(final SourceLocation location, final Expression expression, final Expression index) {
        super(location);

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
    public Type getType() {
        return null; // TODO
    }

    @Override
    public Value evaluate(final Env env) {
        final var eindex = index.evaluate(env);
        final var value = expression.evaluate(env);

        final var i = eindex.getInt();
        return value.getAt(location, i);
    }
}
