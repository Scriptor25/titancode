package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.Value;

public class NumberExpression extends Expression {

    private final double value;

    public NumberExpression(final SourceLocation location, final String value) {
        super(location);

        assert value != null;

        this.value = Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Value evaluate(final Environment env) {
        return new NumberValue(location, value);
    }
}
