package io.scriptor.runtime;

public class StringValue extends Value {

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
    public Value getAt(final int index) {
        assert index >= 0;
        assert index < value.length();
        return new CharValue(value.charAt(index));
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.getString();
    }
}
