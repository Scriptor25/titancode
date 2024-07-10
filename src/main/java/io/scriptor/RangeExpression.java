package io.scriptor;

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
        throw new UnsupportedOperationException();
    }
}
