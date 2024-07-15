package io.scriptor.ast;

import io.scriptor.TitanException;
import io.scriptor.parser.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class BinaryExpression extends Expression {

    public final String operator;
    public final Expression lhs;
    public final Expression rhs;

    public BinaryExpression(
            final SourceLocation location,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        super(location);

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
    public boolean isConstant() {
        return lhs.isConstant() && rhs.isConstant();
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        if (operator.equals("=")) {
            final var value = rhs.evaluate(env);
            if (lhs instanceof IDExpression e) {
                final var name = e.name;
                final var variable = env.setVariable(location, name, value);
                return variable.value;
            }
            if (lhs instanceof IndexExpression e) {
                final var index = e.index.evaluate(env).getInt();
                final var array = e.expression.evaluate(env);
                return array.setAt(index, value);
            }
            if (lhs instanceof MemberExpression e) {
                final var object = e.object.evaluate(env);
                return object.putField(e.member, value);
            }
            throw new TitanException(location, "cannot assign to '%s'", lhs);
        }

        final var left = lhs.evaluate(env);
        final var right = rhs.evaluate(env);
        final var op = env.getBinaryOperator(location, operator, left.getType(location), right.getType(location));
        final var value = op.operator().evaluate(left, right);

        if (op.reassign()) {
            final var name = ((IDExpression) lhs).name;
            final var variable = env.setVariable(location, name, value);
            return variable.value;
        }

        return value;
    }
}
