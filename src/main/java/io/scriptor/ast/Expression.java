package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public abstract class Expression {

    protected static int depth = 0;

    protected static String getSpaces() {
        final var builder = new StringBuilder();
        for (int i = 0; i < depth; ++i)
            builder.append("  ");
        return builder.toString();
    }

    public final SourceLocation location;

    public Expression(final SourceLocation location) {
        assert location != null;
        this.location = location;
    }

    public boolean isConstant() {
        return false;
    }

    public abstract Type getType();

    public abstract Value evaluate(final Env env);
}
