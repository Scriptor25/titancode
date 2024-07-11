package io.scriptor.runtime;

public interface IValue {

    public static IValue fromJava(final Object value) {
        if (value == null)
            throw new NullPointerException();

        if (value instanceof Void)
            return null;

        if (value instanceof Number)
            return new NumberValue(((Number) value).doubleValue());

        if (value instanceof CharSequence)
            return new StringValue(((CharSequence) value).toString());

        throw new IllegalStateException("no type equivalent");
    }

    public Object getValue();

    public default byte getByte() {
        return (byte) (double) (Double) getValue();
    }

    public default char getChar() {
        return (char) (double) (Double) getValue();
    }

    public default short getShort() {
        return (short) (double) (Double) getValue();
    }

    public default int getInt() {
        return (int) (double) (Double) getValue();
    }

    public default long getLong() {
        return (long) (double) (Double) getValue();
    }

    public default float getFloat() {
        return (float) (double) (Double) getValue();
    }

    public default double getDouble() {
        return (Double) getValue();
    }

    public default String getString() {
        return (String) getValue();
    }

    public Type getType();
}
