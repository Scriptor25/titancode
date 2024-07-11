package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class RangeExpression extends Expression {

    public final Expression from;
    public final Expression to;
    public final String id;
    public final Expression expression;

    public RangeExpression(final Expression from, final Expression to, final String id, final Expression expression) {
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
    public Value evaluate(final Env env) {
        final var efrom = from.evaluate(env);
        final var eto = to.evaluate(env);
        for (double i = (Double) efrom.getValue(); i < (Double) eto.getValue(); ++i) {
            final var env1 = new Env(env);
            if (id != null)
                env1.defineVariable(id, Value.fromJava(i));
            expression.evaluate(env1);
        }
        return null;
    }
}
