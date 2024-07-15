package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class ArrayExpression extends Expression {

    public final Expression[] values;

    public ArrayExpression(final SourceLocation location, final Expression[] values) {
        super(location);

        assert values != null;

        this.values = values;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    @Override
    public boolean isConstant() {
        return !Arrays
                .stream(values)
                .anyMatch(value -> !value.isConstant());
    }

    @Override
    public Type getType() {
        return Type.getArray(location);
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        return new ArrayValue(location, Arrays
                .stream(values)
                .map(value -> value.evaluate(env))
                .toArray(Value[]::new));
    }
}
