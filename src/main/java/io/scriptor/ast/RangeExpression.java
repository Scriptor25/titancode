package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class RangeExpression extends Expression {

    public final Expression from;
    public final Expression to;
    public final Expression step;
    public final String id;
    public final Expression expression;

    public RangeExpression(
            final Expression from,
            final Expression to,
            final Expression step,
            final String id,
            final Expression expression) {
        assert from != null;
        assert to != null;
        assert expression != null;

        this.from = from;
        this.to = to;
        this.step = step;
        this.id = id;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("for [%s, %s, %s] -> %s %s", from, to, step, id, expression);
    }

    @Override
    public boolean isConstant() {
        return from.isConstant() && to.isConstant() && (step == null || step.isConstant()) && expression.isConstant();
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        final var efrom = from.evaluate(env).getDouble();
        final var eto = to.evaluate(env).getDouble();
        final var estep = step == null ? 1.0 : step.evaluate(env).getDouble();

        Value result = null;
        for (double i = efrom; i < eto; i += estep) {
            final var env1 = new Env(env);
            if (id != null)
                env1.defineVariable(id, Value.fromJava(i));
            result = expression.evaluate(env1);
        }

        return result;
    }
}
