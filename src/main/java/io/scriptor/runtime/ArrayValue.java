package io.scriptor.runtime;

public class ArrayValue extends Value {

    private final Value[] values;

    public ArrayValue(final Value... values) {
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
    public Value getAt(final int index) {
        assert index >= 0;
        assert index < values.length;
        return values[index];
    }

    @Override
    public Value setAt(final int index, final Value value) {
        assert index >= 0;
        assert index < values.length;
        assert value != null;
        return values[index] = value;
    }

    @Override
    public String getString() {
        // final var builder = new StringBuilder().append("[ ");
        // for (int i = 0; i < values.length; ++i) {
        // if (i > 0)
        // builder.append(", ");
        // builder.append(values[i]);
        // }
        // return builder.append(" ]").toString();
        
        final var builder = new StringBuilder();
        for (final var value : values)
            builder.append(value);
        return builder.toString();
    }

    @Override
    public Type getType() {
        return Type.getArray();
    }
}
