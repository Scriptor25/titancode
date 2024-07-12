package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class BinaryExpression extends Expression {

    public final String operator;
    public final Expression lhs;
    public final Expression rhs;

    public BinaryExpression(final String operator, final Expression lhs, final Expression rhs) {
        assert operator != null;
        assert lhs != null;
        assert rhs != null;

        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", lhs, operator, rhs);
    }

    @Override
    public IValue evaluate(final Env env) {
        assert env != null;

        if (operator.equals("=")) {
            final var name = ((IDExpression) lhs).name;
            final var value = rhs.evaluate(env);
            final var variable = env.setVariable(name, value);
            return variable.value;
        }

        final var left = lhs.evaluate(env);
        final var right = rhs.evaluate(env);
        final var op = env.getBinaryOperator(operator, left.getType(), right.getType());
        final var value = op.operator().evaluate(left, right);

        if (op.reassign()) {
            final var name = ((IDExpression) lhs).name;
            final var variable = env.setVariable(name, value);
            return variable.value;
        }

        return value;
    }
}
