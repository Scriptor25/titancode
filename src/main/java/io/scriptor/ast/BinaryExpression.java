package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.TitanException;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class BinaryExpression extends Expression {

    private static Value assign(
            final SourceLocation location,
            final Environment env,
            final Expression dst,
            final Value src) {
        if (dst instanceof IDExpression e) {
            assert env != null;
            final var name = e.name;
            final var variable = env.setVariable(location, name, src);
            return variable.value;
        }
        if (dst instanceof IndexExpression e) {
            final var index = e.getIndex().evaluate(env).getInt();
            final var array = e.getObject().evaluate(env);
            return array.setAt(index, src);
        }
        if (dst instanceof MemberExpression e) {
            final var object = e.getObject().evaluate(env);
            return object.putField(e.getMember(), src);
        }
        throw new TitanException(location, "cannot assign to '%s'", dst);
    }

    private final String operator;
    private Expression lhs;
    private Expression rhs;

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
    public Expression makeConstant() {
        lhs = lhs.makeConstant();
        rhs = rhs.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        if (operator.equals("=")) {
            final var value = rhs.evaluate(env);
            return assign(location, env, lhs, value);
        }

        final var left = lhs.evaluate(env);
        final var right = rhs.evaluate(env);
        final var op = Environment.getBinaryOperator(
                location,
                operator,
                left.getType(location),
                right.getType(location));
        final var value = op.operator().evaluate(left, right);

        if (op.reassign()) {
            return assign(location, env, lhs, value);
        }

        return value;
    }
}
