package io.scriptor.runtime;

import io.scriptor.parser.SourceLocation;

public abstract class Value {

    public static Value fromJava(final SourceLocation location, final Object value) {
        assert location != null;
        assert value != null;

        if (value instanceof Void)
            return null;

        if (value instanceof Object[] array)
            return new ArrayValue(location, array);

        if (value instanceof Number n)
            return new NumberValue(location, n.doubleValue());

        if (value instanceof Character c)
            return new CharValue(location, c);

        if (value instanceof CharSequence cs)
            return new StringValue(location, cs.toString());

        return new ObjectValue(location, value);
    }

    public final SourceLocation location;

    public Value(final SourceLocation location) {
        assert location != null;
        this.location = location;
    }

    public abstract Object getValue();

    public abstract boolean getBoolean();

    public Value getAt(final SourceLocation location, final int index) {
        throw new UnsupportedOperationException();
    }

    public Value setAt(final int index, final Value value) {
        throw new UnsupportedOperationException();
    }

    public Value getField(final SourceLocation location, final String name) {
        throw new UnsupportedOperationException();
    }

    public Value putField(final String name, final Value value) {
        throw new UnsupportedOperationException();
    }

    public byte getByte() {
        throw new UnsupportedOperationException();
    }

    public char getChar() {
        throw new UnsupportedOperationException();
    }

    public short getShort() {
        throw new UnsupportedOperationException();
    }

    public int getInt() {
        throw new UnsupportedOperationException();
    }

    public long getLong() {
        throw new UnsupportedOperationException();
    }

    public float getFloat() {
        throw new UnsupportedOperationException();
    }

    public double getDouble() {
        throw new UnsupportedOperationException();
    }

    public String getString() {
        throw new UnsupportedOperationException();
    }

    public abstract Type getType(final SourceLocation location);

    @Override
    public String toString() {
        return getString();
    }
}
