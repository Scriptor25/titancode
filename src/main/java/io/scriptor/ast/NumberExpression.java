package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.Value;

public class NumberExpression extends Expression {

    public final double value;

    public NumberExpression(final String value) {
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
    public Value evaluate(final Env env) {
        return new NumberValue(value);
    }
}
