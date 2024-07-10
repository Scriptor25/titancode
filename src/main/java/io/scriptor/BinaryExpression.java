package io.scriptor;

public class BinaryExpression extends Expression {

    public final String operator;
    public final Expression lhs;
    public final Expression rhs;

    public BinaryExpression(final String operator, final Expression lhs, final Expression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", lhs, operator, rhs);
    }

    @Override
    public Value evaluate(final Env env) {
        final var left = lhs.evaluate(env);
        final var right = rhs.evaluate(env);
        final var op = env.getOperator(operator, left.type, right.type);
        return op.evaluate(left, right);
    }
}
