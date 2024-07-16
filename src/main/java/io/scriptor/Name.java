package io.scriptor;

import java.util.HashMap;
import java.util.Map;

public class Name {

    private static final Map<String, Name> names = new HashMap<>();

    public static Name get(final String str) {
        if (names.containsKey(str))
            return names.get(str);

        final var path = str.split(":");
        return new Name(path);
    }

    public static Name get(final String... path) {
        final var str = toString(path);
        if (names.containsKey(str))
            return names.get(str);

        return new Name(path);
    }

    public static Name get(final String[] namespace, final String name) {
        final var str = namespace.length == 0
                ? name
                : toString(namespace) + ':' + name;
        return get(str);
    }

    public static String toString(final String... path) {
        if (path.length == 1)
            return path[0];
        if (path.length == 2)
            return path[0] + ':' + path[1];

        final var builder = new StringBuilder();
        for (int i = 0; i < path.length; ++i) {
            if (i > 0)
                builder.append(':');
            builder.append(path[i]);
        }
        return builder.toString();
    }

    private final String[] path;

    private Name(final String... path) {
        this.path = path;
        names.put(toString(), this);
    }

    @Override
    public String toString() {
        return toString(path);
    }
}
