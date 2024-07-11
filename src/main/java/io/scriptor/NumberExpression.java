package io.scriptor;

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
    public Value evaluate(final Env env) {
        return new NumberValue(value);
    }
}
