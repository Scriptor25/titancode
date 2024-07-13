package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class ArrayExpression extends Expression {

    public final Expression[] values;

    public ArrayExpression(final RLocation location, final Expression[] values) {
        super(location);
        this.values = values;
    }

    @Override
    public Value evaluate(final Env env) {
        return new ArrayValue(
                Arrays
                        .stream(values)
                        .map(value -> value.evaluate(env))
                        .toArray(Value[]::new));
    }
}
