package io.scriptor;

public interface Value {

    public static Value fromJava(final Object value) {
        if (value == null)
            throw new NullPointerException();

        if (value instanceof Double)
            return new NumberValue((Double) value);

        throw new IllegalStateException("no type equivalent");
    }

    public Object getValue();

    public Type getType();
}
