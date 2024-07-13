package io.scriptor.runtime;

import java.util.Arrays;

public class ArrayValue extends Value {

    private final Value[] values;

    public ArrayValue(final Value... values) {
        assert values != null;
        this.values = values;
    }

    public ArrayValue(final Object... values) {
        this(Arrays
                .stream(values)
                .map(value -> Value.fromJava(value))
                .toArray(Value[]::new));
    }

    @Override
    public Object[] getValue() {
        return Arrays
                .stream(values)
                .map(value -> value.getValue())
                .toArray(Object[]::new);
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
    public Value getField(final String name) {
        assert name != null;

        if (name == null)
            throw new IllegalStateException("name must not be null");

        return switch (name) {
            case "size" -> new NumberValue(values.length);
            case "string" -> new StringValue(getString());

            default -> throw new IllegalStateException("no such field");
        };
    }

    @Override
    public String getString() {
        return Arrays.toString(values);
    }

    @Override
    public Type getType() {
        return Type.getArray();
    }
}
