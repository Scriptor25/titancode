package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class ArrayExpression extends Expression {

    private final Expression[] entries;

    public ArrayExpression(final SourceLocation location, final Expression[] entries) {
        super(location);
        assert entries != null;
        this.entries = entries;
    }

    @Override
    public String toString() {
        return Arrays.toString(entries);
    }

    @Override
    public boolean isConstant() {
        return !Arrays
                .stream(entries)
                .anyMatch(value -> !value.isConstant());
    }

    @Override
    public Expression makeConstant() {
        for (int i = 0; i < entries.length; ++i)
            entries[i] = entries[i].makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        return new ArrayValue(location, Arrays
                .stream(entries)
                .map(value -> value.evaluate(env))
                .toArray(Value[]::new));
    }
}
