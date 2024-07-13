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
    public Value getField(final String name) {
        assert name != null;
        assert name.equals("size");

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (!name.equals("size"))
            throw new IllegalStateException("no such field");

        return new NumberValue(value.length());
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
