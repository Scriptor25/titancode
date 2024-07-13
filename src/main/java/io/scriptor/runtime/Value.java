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

        if (value instanceof CharSequence cs)
            return new StringValue(cs.toString());

        return new ObjectValue(value);
    }

    public abstract Object getValue();

    public abstract boolean getBoolean();

    public Value getAt(final int index) {
        throw new IllegalStateException();
    }

    public Value setAt(final int index, final Value value) {
        throw new IllegalStateException();
    }

    public Value getField(final String name) {
        throw new IllegalStateException();
    }

    public Value putField(final String name, final Value value) {
        throw new IllegalStateException();
    }

    public byte getByte() {
        throw new IllegalStateException();
    }

    public char getChar() {
        throw new IllegalStateException();
    }

    public short getShort() {
        throw new IllegalStateException();
    }

    public int getInt() {
        throw new IllegalStateException();
    }

    public long getLong() {
        throw new IllegalStateException();
    }

    public float getFloat() {
        throw new IllegalStateException();
    }

    public double getDouble() {
        throw new IllegalStateException();
    }

    public String getString() {
        throw new IllegalStateException();
    }

    public abstract Type getType();

    @Override
    public String toString() {
        return getString();
    }
}
