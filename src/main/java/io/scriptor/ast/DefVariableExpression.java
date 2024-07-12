package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.Value;

public class DefVariableExpression extends Expression {

    public final String name;
    public final Expression size;
    public final Expression expression;

    public DefVariableExpression(final RLocation location, final String name, final Expression expression) {
        super(location);

        assert name != null;

        this.name = name;
        this.size = null;
        this.expression = expression;
    }

    public DefVariableExpression(
            final RLocation location,
            final String name,
            final Expression size,
            final Expression expression) {
        super(location);

        assert name != null;
        assert size != null;

        this.name = name;
        this.size = size;
        this.expression = expression;
    }

    @Override
    public String toString() {
        if (size != null)
            return String.format("def %s[%s] = %s", name, size, expression);
        return String.format("def %s = %s", name, expression);
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        final Value value;
        if (expression != null) {
            value = expression.evaluate(env);
        } else {
            value = new NumberValue(0);
        }

        if (size != null) {
            final var esize = size.evaluate(env).getInt();
            final var values = new Value[esize];
            Arrays.fill(values, value);
            final var array = new ArrayValue(values);
            env.defineVariable(name, array);
            return null;
        }

        env.defineVariable(name, value);
        return null;
    }
}
