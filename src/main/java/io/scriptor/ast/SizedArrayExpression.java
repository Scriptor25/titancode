package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.Value;

public class SizedArrayExpression extends Expression {

    private Expression size;
    private Expression init;

    public SizedArrayExpression(final SourceLocation location, final Expression size, final Expression init) {
        super(location);
        assert size != null;
        this.size = size;
        this.init = init;
    }

    @Override
    public boolean isConstant() {
        return size.isConstant() && init.isConstant();
    }

    @Override
    public Expression makeConstant() {
        size = size.makeConstant();
        if (init != null)
            init = init.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
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
