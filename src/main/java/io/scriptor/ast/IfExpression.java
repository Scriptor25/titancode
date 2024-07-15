package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class IfExpression extends Expression {

    public final Expression condition;
    public final Expression branchTrue;
    public final Expression branchFalse;

    public IfExpression(
            final SourceLocation location,
            final Expression condition,
            final Expression branchTrue,
            final Expression branchFalse) {
        super(location);

        assert condition != null;
        assert branchTrue != null;

        this.condition = condition;
        this.branchTrue = branchTrue;
        this.branchFalse = branchFalse;
    }

    @Override
    public String toString() {
        return String.format("if [%s] %s else %s", condition, branchTrue, branchFalse);
    }

    @Override
    public boolean isConstant() {
        return condition.isConstant()
                && branchTrue.isConstant()
                && (branchFalse == null || branchFalse.isConstant());
    }

    @Override
    public Type getType() {
        return null; // TODO
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        final var c = condition.evaluate(env);
        if (c.getBoolean())
            return branchTrue.evaluate(env);

        if (branchFalse != null)
            return branchFalse.evaluate(env);

        return null;
    }
}
