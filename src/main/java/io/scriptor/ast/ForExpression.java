package io.scriptor.ast;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class ForExpression extends Expression {

    private Expression from;
    private Expression to;
    private Expression step;
    private final String id;
    private Expression body;

    public ForExpression(
            final SourceLocation location,
            final Expression from,
            final Expression to,
            final Expression step,
            final String id,
            final Expression body) {
        super(location);

        assert from != null;
        assert to != null;
        assert body != null;

        this.from = from;
        this.to = to;
        this.step = step;
        this.id = id;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format(
                "for [%s, %s%s]%s %s",
                from,
                to,
                step == null ? "" : ", " + step.toString(),
                id == null ? "" : " -> " + id,
                body);
    }

    @Override
    public boolean isConstant() {
        return from.isConstant()
                && to.isConstant()
                && (step == null || step.isConstant())
                && body.isConstant();
    }

    @Override
    public Expression makeConstant() {
        from = from.makeConstant();
        to = to.makeConstant();
        if (step != null)
            step = step.makeConstant();
        body = body.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        final var f = from.evaluate(env).getDouble();
        final var t = to.evaluate(env).getDouble();
        final var s = step == null ? 1.0 : step.evaluate(env).getDouble();

        Value result = null;
        for (double i = f; i < t; i += s) {
            final var env1 = new Environment(env);
            if (id != null)
                env1.defineVariable(location, Name.get(id), Value.fromJava(location, i));
            result = body.evaluate(env1);
        }

        return result;
    }
}
