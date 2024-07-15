package io.scriptor.runtime;

import io.scriptor.TitanException;
import io.scriptor.parser.SourceLocation;

public class NumberValue extends Value {

    private final double value;

    public NumberValue(final SourceLocation location, final double value) {
        super(location);
        this.value = value;
    }

    public NumberValue(final SourceLocation location, final boolean value) {
        super(location);
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
    public Value getField(final SourceLocation location, final String name) {
        assert name != null;
        return switch (name) {
            case "string" -> new StringValue(location, getString());
            default -> throw new TitanException(location, "no such field: %s", name);
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
    public Type getType(final SourceLocation location) {
        return Type.getNumber(location);
    }
}
