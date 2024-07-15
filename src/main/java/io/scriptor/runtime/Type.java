package io.scriptor.runtime;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.SourceLocation;
import io.scriptor.TitanException;

public class Type {

    private static final Map<String, Type> types = new HashMap<>();

    static {
        new Type("number");
        new Type("string");
        new Type("array");
        new Type("char");
        new Type("object");
        new Type("function");
    }

    public static Type get(final SourceLocation location, final String name) {
        assert name != null;
        if (!types.containsKey(name))
            throw new TitanException(location, "no such type: %s", name);
        return types.get(name);
    }

    public static Type getNumber(final SourceLocation location) {
        return get(location, "number");
    }

    public static Type getString(final SourceLocation location) {
        return get(location, "string");
    }

    public static Type getArray(final SourceLocation location) {
        return get(location, "array");
    }

    public static Type getChar(final SourceLocation location) {
        return get(location, "char");
    }

    public static Type getObject(final SourceLocation location) {
        return get(location, "object");
    }

    public static Type getFunction(final SourceLocation location) {
        return get(location, "function");
    }

    public final String name;

    private Type(final String name) {
        assert name != null;
        this.name = name;
        types.put(name, this);
    }
}
