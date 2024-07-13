package io.scriptor.runtime;

public class NumberValue extends Value {

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
    public Value getField(final String name) {
        assert name != null;

        if (name == null)
            throw new IllegalStateException("name must not be null");

        return switch (name) {
            case "string" -> new StringValue(getString());

            default -> throw new IllegalStateException("no such field");
        };
    }

    @Override
    public byte getByte() {
        return (byte) value;
    }

    @Override
    public char getChar() {
        return (char) value;
    }

    @Override
    public short getShort() {
        return (short) value;
    }

    @Override
    public int getInt() {
        return (int) value;
    }

    @Override
    public long getLong() {
        return (long) value;
    }

    @Override
    public float getFloat() {
        return (float) value;
    }

    @Override
    public double getDouble() {
        return value;
    }

    @Override
    public String getString() {
        return Double.toString(value);
    }

    @Override
    public Type getType() {
        return Type.getNumber();
    }
}
