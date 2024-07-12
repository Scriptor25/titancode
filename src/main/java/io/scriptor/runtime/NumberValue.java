package io.scriptor.runtime;

public class NumberValue implements IValue {

    private final double value;

    public NumberValue(final double value) {
        this.value = value;
    }

    public NumberValue(final boolean value) {
        this.value = value ? 1.0 : 0.0;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public boolean getBoolean() {
        return value != 0;
    }

    @Override
    public Type getType() {
        return Type.getNumber();
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
