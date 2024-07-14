package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

public class Type {

    private static final Map<String, Type> types = new HashMap<>();

    static {
        new Type("number");
        new Type("string");
        new Type("array");
        new Type("char");
        new Type("object");
    }

    public static Type get(final String name) {
        assert name != null;
        if (!types.containsKey(name))
            throw new RuntimeException("no such type");
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

    public static Type getChar() {
        return get("char");
    }

    public static Type getObject() {
        return get("object");
    }

    public final String name;

    private Type(final String name) {
        assert name != null;
        this.name = name;
        types.put(name, this);
    }
}
