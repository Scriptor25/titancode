package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class ConstExpression extends Expression {

    public static Expression makeConst(final Expression expression) {
        assert expression != null;
        if (expression.isConstant() && !(expression instanceof ConstExpression))
            return new ConstExpression(expression);
        return expression;
    }

    public final Expression expression;
    public final Value value;

    private ConstExpression(final Expression expression) {
        super(expression.location);

        assert expression != null;
        assert expression.isConstant();

        this.expression = expression;
        this.value = expression.evaluate(new Environment());
    }

    @Override
    public String toString() {
        final var type = value == null ? "?" : value.getType(SourceLocation.UNKNOWN).name;
        return String.format("[const (%s) %s]", type, value);
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Expression makeConstant() {
        return this;
    }

    @Override
    public Value evaluate(final Environment env) {
        return value;
    }
}
