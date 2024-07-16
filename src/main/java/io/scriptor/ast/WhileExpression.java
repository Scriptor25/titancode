package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class WhileExpression extends Expression {

    private Expression condition;
    private Expression expression;

    public WhileExpression(final SourceLocation location, final Expression condition, final Expression expression) {
        super(location);

        assert condition != null;
        assert expression != null;

        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("while [%s] %s", condition, expression);
    }

    @Override
    public boolean isConstant() {
        return condition.isConstant() && expression.isConstant();
    }

    @Override
    public Expression makeConstant() {
        condition = condition.makeConstant();
        expression = expression.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        Value result = null;
        while (true) {
            final var c = condition.evaluate(env);
            if (!c.getBoolean())
                break;
            result = expression.evaluate(env);
        }

        return result;
    }
}
