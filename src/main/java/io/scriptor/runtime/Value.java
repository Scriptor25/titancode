package io.scriptor.runtime;

public abstract class Value {

    public static Value fromJava(final Object value) {
        assert value != null;

        if (value instanceof Void)
            return null;

        if (value instanceof Object[] array)
            return new ArrayValue(array);

        if (value instanceof Number n)
            return new NumberValue(n.doubleValue());

        if (value instanceof Character c)
            return new CharValue(c);

        if (value instanceof CharSequence cs)
            return new StringValue(cs.toString());

        return new ObjectValue(value);
    }

    public abstract Object getValue();

    public abstract boolean getBoolean();

    public Value getAt(final int index) {
        throw new UnsupportedOperationException();
    }

    public Value setAt(final int index, final Value value) {
        throw new UnsupportedOperationException();
    }

    public Value getField(final String name) {
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

    public abstract Type getType();

    @Override
    public String toString() {
        return getString();
    }
}
