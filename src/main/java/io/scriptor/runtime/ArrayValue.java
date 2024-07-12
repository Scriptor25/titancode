package io.scriptor.runtime;

public class ArrayValue implements IValue {

    private final IValue[] values;

    public ArrayValue(final IValue... values) {
        assert values != null;

        this.values = values;
    }

    @Override
    public Object[] getValue() {
        final var objects = new Object[values.length];
        for (int i = 0; i < objects.length; ++i)
            objects[i] = values[i].getValue();
        return objects;
    }

    @Override
    public boolean getBoolean() {
        return values.length > 0;
    }

    @Override
    public Type getType() {
        return Type.getArray();
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder().append("[ ");
        for (int i = 0; i < values.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(values[i]);
        }
        return builder.append(" ]").toString();
    }
}
