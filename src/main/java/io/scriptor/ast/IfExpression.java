package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class IfExpression extends Expression {

    private Expression condition;
    private Expression branchTrue;
    private Expression branchFalse;

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
    public Expression makeConstant() {
        condition = condition.makeConstant();
        branchTrue = branchTrue.makeConstant();
        if (branchFalse != null)
            branchFalse = branchFalse.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        final var c = condition.evaluate(env);
        if (c.getBoolean())
            return branchTrue.evaluate(env);

        if (branchFalse != null)
            return branchFalse.evaluate(env);

        return null;
    }
}
