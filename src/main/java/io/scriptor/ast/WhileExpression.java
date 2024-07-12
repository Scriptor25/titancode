package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class WhileExpression extends Expression {

    public final Expression condition;
    public final Expression expression;

    public WhileExpression(final Expression condition, final Expression expression) {
        assert condition != null;
        assert expression != null;

        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

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
