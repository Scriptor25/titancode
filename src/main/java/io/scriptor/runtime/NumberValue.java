package io.scriptor.runtime;

public class NumberValue implements Value {

    private final double value;

    public NumberValue(final double value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.getNumber();
    }

}
