package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class WhileExpression extends Expression {

    public final Expression condition;
    public final Expression expression;

    public WhileExpression(final Expression condition, final Expression expression) {
        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public IValue evaluate(final Env env) {
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }
}
