package io.scriptor.ast;

import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class ConstExpression extends Expression {

    public static Expression makeConst(final Expression expression) {
        if (expression.isConstant())
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
        return expression.toString();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Type getType() {
        return value.getType(location);
    }

    @Override
    public Value evaluate(final Environment env) {
        return value;
    }
}
