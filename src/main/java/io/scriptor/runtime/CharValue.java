package io.scriptor.runtime;

import io.scriptor.TitanException;
import io.scriptor.parser.SourceLocation;

public class CharValue extends Value {

    private final char value;

    public CharValue(final SourceLocation location, final char value) {
        super(location);
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
    public Type getType(final SourceLocation location) {
        return Type.getChar(location);
    }
}
