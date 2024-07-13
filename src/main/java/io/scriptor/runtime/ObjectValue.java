package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue extends Value {

    private final Map<String, Value> fields = new HashMap<>();

    public ObjectValue() {
    }

    public ObjectValue(final Map<String, Value> fields) {
        this.fields.putAll(fields);
    }

    public ObjectValue(final Object object) {
        for (final var field : object.getClass().getFields())
            if (field.canAccess(object))
                try {
                    fields.put(field.getName(), fromJava(field.get(object)));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
    }

    @Override
    public Map<String, Object> getValue() {
        final Map<String, Object> object = new HashMap<>();
        for (final var entry : fields.entrySet())
            object.put(entry.getKey(), entry.getValue().getValue());
        return object;
    }

    @Override
    public boolean getBoolean() {
        return !fields.isEmpty();
    }

    @Override
    public Value getAt(final int index) {
        assert index >= 0;
        assert index < fields.size();

        if (index < 0)
            throw new IllegalStateException("index must be >= 0");

        if (index >= fields.size())
            throw new IllegalStateException("index must be < size");

        return fields.values().toArray(Value[]::new)[index];
    }

    @Override
    public Value setAt(final int index, final Value value) {
        assert index >= 0;
        assert index < fields.size();
        assert value != null;

        if (index < 0)
            throw new IllegalStateException("index must be >= 0");

        if (index >= fields.size())
            throw new IllegalStateException("index must be < size");

        if (value == null)
            throw new IllegalStateException("value must not be null");

        final var key = fields.keySet().toArray(String[]::new)[index];
        fields.put(key, value);
        return value;
    }

    @Override
    public Value getField(final String name) {
        assert name != null;
        assert name.equals("string") || fields.containsKey(name);

        if (name == null)
            throw new IllegalStateException("name must not be null");

        switch (name) {
            case "string" -> {
                return new StringValue(getString());
            }
        }

        if (!fields.containsKey(name))
            throw new IllegalStateException("no such field");

        return fields.get(name);
    }

    @Override
    public Value putField(final String name, final Value value) {
        assert name != null;
        assert value != null;

        if (name == null)
            throw new IllegalStateException("name must not be null");

        if (value == null)
            throw new IllegalStateException("value must not be null");

        fields.put(name, value);
        return value;
    }

    @Override
    public String getString() {
        final var builder = new StringBuilder().append('{');
        boolean first = true;
        for (final var field : fields.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(field.getKey()).append('=').append(field.getValue());
        }
        return builder.append('}').toString();
    }

    @Override
    public Type getType() {
        return Type.getObject();
    }
}
