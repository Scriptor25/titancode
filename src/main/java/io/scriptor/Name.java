package io.scriptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Name {

    private static final Map<String, Name> names = new HashMap<>();

    public static Name get(final String str) {
        if (names.containsKey(str))
            return names.get(str);

        final var path = str.split(":");
        final var namespace = new Namespace(path.length > 1
                ? Arrays.copyOfRange(path, 0, path.length - 1)
                : new String[0]);
        final var name = path[path.length - 1];

        return new Name(namespace, name);
    }

    public static Name get(final Namespace namespace, final String name) {
        final var str = namespace.path.length == 0
                ? name
                : namespace + ":" + name;
        if (names.containsKey(str))
            return names.get(str);

        return new Name(namespace, name);
    }

    public final Namespace namespace;
    public final String name;

    public Name(final Namespace namespace, final String name) {
        this.namespace = namespace;
        this.name = name;
        names.put(toString(), this);
    }

    @Override
    public String toString() {
        if (namespace.path.length == 0)
            return name;
        return String.format("%s:%s", namespace, name);
    }
}
