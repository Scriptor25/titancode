package io.scriptor.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ObjectValue extends Value {

    private final Map<String, Value> fields = new HashMap<>();

    public ObjectValue() {
    }

    public ObjectValue(final Map<String, Value> fields) {
        assert fields != null;
        this.fields.putAll(fields);
    }

    public ObjectValue(final Object object) {
        assert object != null;
        for (final var field : object.getClass().getDeclaredFields())
            try {
                fields.put(field.getName(), fromJava(field.get(object)));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBoolean() {
        return !fields.isEmpty();
    }

    @Override
    public Value getAt(final int index) {
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
    public Value getField(final String name) {
        assert name != null;
        switch (name) {
            case "string" -> {
                return new StringValue(getString());
            }
        }

        if (!fields.containsKey(name))
            throw new RuntimeException("no such field");

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
    public Type getType() {
        return Type.getObject();
    }
}
