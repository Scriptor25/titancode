package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.IValue;

public class NumberExpression extends Expression {

    public final double value;

    public NumberExpression(final String value) {
        this.value = Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public IValue evaluate(final Env env) {
        return new NumberValue(value);
    }
}
