package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.Value;

public class SizedArrayExpression extends Expression {

    public final Expression size;
    public final Expression init;

    public SizedArrayExpression(final SourceLocation location, final Expression size, final Expression init) {
        super(location);

        assert size != null;

        this.size = size;
        this.init = init;
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        final var n = size.evaluate(env).getInt();
        final var values = new Value[n];
        if (init == null)
            Arrays.fill(values, new NumberValue(location, 0));
        else
            for (int i = 0; i < n; ++i)
                values[i] = init.evaluate(env);
        return new ArrayValue(location, values);
    }
}
