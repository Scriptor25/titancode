package io.scriptor.runtime;

import io.scriptor.SourceLocation;
import io.scriptor.TitanException;

public class StringValue extends Value {

    private final String value;

    public StringValue(final SourceLocation location, final String value) {
        super(location);
        assert value != null;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean getBoolean() {
        return !value.isEmpty();
    }

    @Override
    public Value getAt(final SourceLocation location, final int index) {
        assert index >= 0;
        assert index < value.length();
        return new CharValue(location, value.charAt(index));
    }

    @Override
    public Value getField(final SourceLocation location, final String name) {
        assert name != null;
        return switch (name) {
            case "size" -> new NumberValue(location, value.length());
            default -> throw new TitanException(location, "no such field: %s", name);
        };
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public Type getType(final SourceLocation location) {
        return Type.getString(location);
    }
}
