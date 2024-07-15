package io.scriptor.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import io.scriptor.SourceLocation;
import io.scriptor.TitanException;

public class ObjectValue extends Value {

    private final Map<String, Value> fields = new HashMap<>();

    public ObjectValue(final SourceLocation location) {
        super(location);
    }

    public ObjectValue(final SourceLocation location, final Map<String, Value> fields) {
        super(location);
        assert fields != null;
        this.fields.putAll(fields);
    }

    public ObjectValue(final SourceLocation location, final Object object) {
        super(location);
        assert object != null;
        for (final var field : object.getClass().getDeclaredFields())
            try {
                fields.put(field.getName(), fromJava(location, field.get(object)));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new TitanException(location, e);
            }
    }

    @Override
    public Map<String, Object> getValue() {
        final Map<String, Object> object = new HashMap<>();
        for (final var entry : fields.entrySet())
            object.put(entry.getKey(), entry.getValue().getValue());
        return object;
    }

    public <T> T getAs(final Class<T> clazz) {
        assert clazz != null;
        try {
            final var object = clazz.getDeclaredConstructor().newInstance();
            for (final var field : fields.entrySet())
                clazz.getDeclaredField(field.getKey()).set(object, field.getValue().getValue());
            return object;
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException
                | NoSuchFieldException e) {
            throw new TitanException(location, e);
        }
    }

    @Override
    public boolean getBoolean() {
        return !fields.isEmpty();
    }

    @Override
    public Value getAt(final SourceLocation location, final int index) {
        assert index >= 0;
        assert index < fields.size();
        return fields.values().toArray(Value[]::new)[index];
    }

    @Override
    public Value setAt(final int index, final Value value) {
        assert index >= 0;
        assert index < fields.size();
        assert value != null;
        final var key = fields.keySet().toArray(String[]::new)[index];
        fields.put(key, value);
        return value;
    }

    @Override
    public Value getField(final SourceLocation location, final String name) {
        assert name != null;
        switch (name) {
            case "string" -> {
                return new StringValue(location, getString());
            }
        }

        if (!fields.containsKey(name))
            throw new TitanException(location, "no such field: %s", name);

        return fields.get(name);
    }

    @Override
    public Value putField(final String name, final Value value) {
        assert name != null;
        assert value != null;
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
    public Type getType(final SourceLocation location) {
        return Type.getObject(location);
    }
}
