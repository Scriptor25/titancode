package io.scriptor;

import java.util.HashMap;
import java.util.Map;

public class Type {

    private static final Map<String, Type> types = new HashMap<>();

    static {
        new Type("number");
    }

    public static Type get(final String name) {
        if (!types.containsKey(name))
            throw new IllegalStateException(String.format("'%s' is not a type", name));
        return types.get(name);
    }

    public static Type getNumber() {
        return get("number");
    }

    public final String name;

    private Type(final String name) {
        this.name = name;
        types.put(name, this);
    }
}
