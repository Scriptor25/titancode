package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class IfExpression extends Expression {

    public final Expression condition;
    public final Expression branchTrue;
    public final Expression branchFalse;

    public IfExpression(final Expression condition, final Expression branchTrue, final Expression branchFalse) {
        this.condition = condition;
        this.branchTrue = branchTrue;
        this.branchFalse = branchFalse;
    }

    @Override
    public IValue evaluate(final Env env) {
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }
}
