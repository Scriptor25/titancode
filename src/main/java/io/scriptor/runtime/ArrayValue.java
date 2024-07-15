package io.scriptor.runtime;

import java.util.Arrays;

import io.scriptor.TitanException;
import io.scriptor.parser.SourceLocation;

public class ArrayValue extends Value {

    private final Value[] values;

    public ArrayValue(final SourceLocation location, final Value[] values) {
        super(location);
        assert values != null;
        this.values = values;
    }

    public ArrayValue(final SourceLocation location, final Object[] values) {
        this(location, Arrays
                .stream(values)
                .map(value -> Value.fromJava(location, value))
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
    public Value getAt(final SourceLocation location, final int index) {
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
    public Value getField(final SourceLocation location, final String name) {
        assert name != null;
        return switch (name) {
            case "size" -> new NumberValue(location, values.length);
            case "string" -> new StringValue(location, getString());
            default -> throw new TitanException(location, "no such field: %s", name);
        };
    }

    @Override
    public String getString() {
        return Arrays.toString(values);
    }

    @Override
    public Type getType(final SourceLocation location) {
        return Type.getArray(location);
    }
}
