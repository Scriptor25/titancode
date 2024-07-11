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

    public Type getType();
}
