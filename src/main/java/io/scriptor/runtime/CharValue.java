package io.scriptor.runtime;

public class CharValue extends Value {

    private final char value;

    public CharValue(final char value) {
        this.value = value;
    }

    @Override
    public Character getValue() {
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
        return value;
    }

    @Override
    public short getShort() {
        return (short) value;
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public long getLong() {
        return value;
    }

    @Override
    public float getFloat() {
        return value;
    }

    @Override
    public double getDouble() {
        return value;
    }

    @Override
    public String getString() {
        return Character.toString(value);
    }

    @Override
    public Type getType() {
        return Type.getChar();
    }
}
