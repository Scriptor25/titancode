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
    public final Expression init;

    public DefVariableExpression(final RLocation location, final String name, final Expression init) {
        super(location);

        assert name != null;

        this.name = name;
        this.size = null;
        this.init = init;
    }

    public DefVariableExpression(
            final RLocation location,
            final String name,
            final Expression size,
            final Expression init) {
        super(location);

        assert name != null;
        assert size != null;

        this.name = name;
        this.size = size;
        this.init = init;
    }

    @Override
    public String toString() {
        if (size != null)
            return String.format("def %s[%s] = %s", name, size, init);
        return String.format("def %s = %s", name, init);
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        if (size != null) {
            final var n = size.evaluate(env).getInt();
            final var values = new Value[n];
            if (init == null)
                Arrays.fill(values, new NumberValue(0));
            else
                for (int i = 0; i < n; ++i)
                    values[i] = init.evaluate(env);
            final var array = new ArrayValue(values);
            env.defineVariable(name, array);
            return array;
        }

        final var value = init == null ? new NumberValue(0) : init.evaluate(env);
        env.defineVariable(name, value);
        return value;
    }
}
