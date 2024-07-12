package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class RangeExpression extends Expression {

    public final Expression from;
    public final Expression to;
    public final String id;
    public final Expression expression;

    public RangeExpression(final Expression from, final Expression to, final String id, final Expression expression) {
        assert from != null;
        assert to != null;
        assert expression != null;

        this.from = from;
        this.to = to;
        this.id = id;
        this.expression = expression;
    }

    @Override
    public String toString() {
        if (id == null)
            return String.format("[%s, %s] %s", from, to, expression);
        return String.format("[%s, %s]{%s} %s", from, to, id, expression);
    }

    @Override
    public IValue evaluate(final Env env) {
        assert env != null;

        final var efrom = from.evaluate(env).getDouble();
        final var eto = to.evaluate(env).getDouble();

        IValue result = null;
        for (double i = efrom; i < eto; ++i) {
            final var env1 = new Env(env);
            if (id != null)
                env1.defineVariable(id, IValue.fromJava(i));
            result = expression.evaluate(env1);
        }

        return result;
    }
}
