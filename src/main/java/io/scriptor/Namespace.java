package io.scriptor;

public class Namespace {

    public final String[] path;

    public Namespace(final String... path) {
        this.path = path;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < path.length; ++i) {
            if (i > 0)
                builder.append(':');
            builder.append(path[i]);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null)
            return false;
        if (!(object instanceof Namespace ns))
            return false;
        if (this.path.length != ns.path.length)
            return false;
        for (int i = 0; i < this.path.length; ++i)
            if (!this.path[i].equals(ns.path[i]))
                return false;
        return true;
    }
}
