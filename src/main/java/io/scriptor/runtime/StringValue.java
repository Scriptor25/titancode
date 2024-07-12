package io.scriptor.runtime;

public class StringValue implements IValue {

    private final String value;

    public StringValue(final String value) {
        assert value != null;

        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean getBoolean() {
        return !value.isEmpty();
    }

    @Override
    public Type getType() {
        return Type.getString();
    }

    @Override
    public String toString() {
        return value;
    }
}
