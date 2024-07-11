package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

public class Type {

    private static final Map<String, Type> types = new HashMap<>();

    static {
        new Type("number");
        new Type("string");
        new Type("array");
    }

    public static Type get(final String name) {
        if (!types.containsKey(name))
            throw new IllegalStateException(String.format("'%s' is not a type", name));
        return types.get(name);
    }

    public static Type getNumber() {
        return get("number");
    }

    public static Type getString() {
        return get("string");
    }

    public static Type getArray() {
        return get("array");
    }

    public final String name;

    private Type(final String name) {
        this.name = name;
        types.put(name, this);
    }
}
